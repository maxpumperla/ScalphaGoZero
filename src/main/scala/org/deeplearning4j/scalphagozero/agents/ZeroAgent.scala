package org.deeplearning4j.scalphagozero.agents

import org.deeplearning4j.nn.graph.ComputationGraph
import org.deeplearning4j.scalphagozero.board.{ GameState, Move }
import org.deeplearning4j.scalphagozero.encoders.ZeroEncoder
import org.deeplearning4j.scalphagozero.experience.{ ZeroExperienceBuffer, ZeroExperienceCollector }
import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.factory.Nd4j
import scala.util.Random
import ZeroAgent.{ DEBUG, RND }

/**
  * AlphaGo Zero agent, main workhorse of this project. ZeroAgent implements the characteristic combination of
  * tree search with reinforcement learning that lead to breakthrough results for the game of Go (AlphaGo Zero)
  * and other board games like chess (Alpha Zero).
  *
  * @param model DL4J computation graph suitable for AGZ predictions
  * @param encoder ZeroEncoder instance to feed data into the model
  * @param roundsPerMove roll-outs per move
  * @param c constant to multiply score by (defaults to 2.0)
  *
  * @author Max Pumperla
  * @author Barry Becker
  */
class ZeroAgent(
    val model: ComputationGraph,
    val encoder: ZeroEncoder,
    val roundsPerMove: Int = 20,
    val c: Double = 2.0,
    val rand: Random = RND
) extends Agent {

  val collector: ZeroExperienceCollector = new ZeroExperienceCollector()

  /**
    * @return the best move selected by the trained model
    */
  override def selectMove(gameState: GameState): Move = {
    val root = createNode(gameState)

    for (_ <- 0 until roundsPerMove) {
      var node: Option[ZeroTreeNode] = Some(root)
      var nextMove = selectBranch(node.get)
      // identify the branch in the current tree to add a new child to
      while (node.get.hasChild(nextMove)) {
        node = node.get.getChild(nextMove)
        nextMove = selectBranch(node.get)
      }
      val newState = node.get.gameState.applyMove(nextMove)
      val childNode = createNode(newState, Some(nextMove), node)
      var move: Option[Move] = Some(nextMove)
      var value = -childNode.value
      // record for ancestor nodes
      while (node.isDefined && move.isDefined) {
        node.get.recordVisit(move.get, value)
        move = node.get.lastMove
        node = node.get.parent
        value = -value
      }
    }

    recordVisitCounts(root)

    val validMoves = root.moves.filter(m => m != Move.Pass && gameState.isValidMove(m))
    val selected = selectValidNextMove(validMoves, root)
    if (DEBUG) {
      println("totalVisitCt = " + root.totalVisitCount)
      println("Selected " + selected + " from these valid moves:  ")
      println(validMoves.map(m => (m, root.visitCount(m))).mkString(", "))
    }

    selected
  }

  private def recordVisitCounts(root: ZeroTreeNode): Unit = {
    //if (DEBUG) println(root)  // print the whole MCT
    val rootStateTensor = encoder.encode(root.gameState)
    val visitCounts: INDArray = Nd4j.create(1, encoder.numMoves)
    for (index <- 0 until encoder.numMoves) {
      val move: Move = encoder.decodeMoveIndex(index)
      visitCounts.put(1, index, Nd4j.scalar(root.visitCount(move).doubleValue()))
    }
    collector.recordDecision(rootStateTensor, visitCounts)
  }

  /**
    * @return move that is randomly selected from among those that were visited most visited.
    */
  private def selectValidNextMove(validMoves: Seq[Move], root: ZeroTreeNode): Move =
    if (validMoves.isEmpty) Move.Pass
    else {
      val movesWithVisitCounts = validMoves.map(m => (m, root.visitCount(m)))
      val maxVisits = movesWithVisitCounts.maxBy(_._2)._2
      val maxVisitMoves = movesWithVisitCounts.filter(_._2 == maxVisits)
      if (maxVisitMoves.isEmpty) Move.Pass
      else maxVisitMoves(RND.nextInt(maxVisitMoves.length))._1
    }

  /**
    * Select a move given a node.
    * The branch is based on maximizing the utility function described around page 282 in the ML for Go book.
    *
    * @param node ZeroTreeNode
    * @return Move instance with the highest utility score (i.e. chance of winning)
    */
  def selectBranch(node: ZeroTreeNode): Move = {
    val totalCount = node.totalVisitCount

    // Utility function to update the summary statistics as described on page 282 of DL for Go book
    def scoreBranch(move: Move): Double = {
      val q = node.expectedValue(move) // ratio of wins to losses for this node
      val p = node.prior(move) // probability of winning from the model. Weighted less as visits increase.
      val n = node.visitCount(move)
      q + this.c * p * Math.sqrt(totalCount.doubleValue()) / (n + 1)
    }

    if (node.moves.isEmpty) {
      //if (DEBUG) println(s"There are no moves for ${node.gameState.nextPlayer} from this position.")
      Move.Pass
    } else {
      val movesWithScore = node.moves
        .map(m => (m, scoreBranch(m)))
      movesWithScore.maxBy(_._2)._1
    }
  }

  /**
    * Create a new ZeroTreeNode from the current game state
    * and use the model predictions to initialize the movePriors.
    *
    * @param gameState game state
    * @param move optional move
    * @param parent optional parent ZeroTreeNode
    * @return ZeroTreeNode
    */
  def createNode(gameState: GameState, move: Option[Move] = None, parent: Option[ZeroTreeNode] = None): ZeroTreeNode = {
    val stateTensor: INDArray = encoder.encode(gameState)
    val outputs = model.output(stateTensor)
    val priors = outputs(0)
    val value = outputs(1).getDouble(0L, 0L)

    var movePriors: Map[Move, Double] = Map[Move, Double]()
    for (i <- 0 until priors.length().toInt) {
      val move = encoder.decodeMoveIndex(i.toInt)
      val prior = priors.getDouble(i.toLong)
      movePriors += (move -> prior)
    }

    val newNode = new ZeroTreeNode(gameState, value, movePriors, parent, move)
    if (parent.isDefined && move.isDefined) {
      parent.get.addChild(move.get, newNode)
    }
    newNode
  }

  /**
    * Learn from experience, after the play-out is done.
    *
    * @param experience ZeroExperienceBuffer
    */
  def train(experience: ZeroExperienceBuffer): Unit = {
    val numExamples = experience.states.shape()(0).toInt

    val modelInput: INDArray = experience.states

    val countLength = experience.visitCounts.shape()(1)
    val visitSums = Nd4j.sum(experience.visitCounts, 1).reshape(Array[Int](numExamples, 1))

    val actionTarget = experience.visitCounts.div(visitSums.repeat(1, countLength))
    val valueTarget = experience.rewards
    if (DEBUG) {
      println("visitSums:\n" + visitSums.toDoubleVector.mkString(", "))
      println("\nactionTarget shape = " + actionTarget.shape().mkString(", "))
      println()
      println("valueTarget:\n" + valueTarget.toDoubleVector.mkString(", "))
    }

    model.fit(Array[INDArray](modelInput), Array[INDArray](actionTarget, valueTarget))
  }
}

object ZeroAgent {
  private val DEBUG = false
  private val RND = new Random(1)
}
