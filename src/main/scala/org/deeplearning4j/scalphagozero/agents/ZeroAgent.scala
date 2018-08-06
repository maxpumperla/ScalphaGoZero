package org.deeplearning4j.scalphagozero.agents
import org.deeplearning4j.nn.graph.ComputationGraph
import org.deeplearning4j.scalphagozero.board.{ GameState, Move }
import org.deeplearning4j.scalphagozero.encoders.{ Encoder, ZeroEncoder }
import org.deeplearning4j.scalphagozero.experience.{ ZeroExperienceBuffer, ZeroExperienceCollector }
import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.factory.Nd4j

import scala.collection.mutable

/**
  * AlphaGo Zero agent
  *
  * @author Max Pumperla
  */
class ZeroAgent(val model: ComputationGraph,
                val encoder: ZeroEncoder,
                val roundsPerMove: Int = 1000,
                val c: Double = 2.0)
    extends Agent {

  var collector = new ZeroExperienceCollector

  def setCollector(collector: ZeroExperienceCollector): Unit = this.collector = collector

  override def selectMove(gameState: GameState): Move = Move.pass() // TODO

  def selectBranch(node: ZeroTreeNode): Move = {
    val totalCount = node.totalVisitCount

    def scoreBranch(move: Move): Double = {
      val q = node.expectedValue(move)
      val p = node.prior(move)
      val n = node.visitCount(move)
      q + this.c * p * Math.sqrt(totalCount) / (n + 1)
    }

    val mv: Move = node.moves
      .map(m => (m, scoreBranch(m)))
      .reduce((m1, m2) => if (m1._2 > m2._2) m1 else m2)
      ._1
    mv
  }

  def createNode(gameState: GameState, move: Move, parent: Option[ZeroTreeNode]): ZeroTreeNode = {
    val stateTensor: INDArray = this.encoder.encode(gameState)
    val outputs = this.model.output(stateTensor)
    val priors = outputs(0)
    val value = outputs(1)

    val movePriors: mutable.Map[Move, Double] = new mutable.HashMap[Move, Double]()
    for (i <- 0 until priors.length().toInt) {
      val move = encoder.decodeMoveIndex(i.toInt)
      val prior = priors.getDouble(i.toLong)
      movePriors.put(move, prior)
    }

    val newNode = new ZeroTreeNode(gameState, 0, movePriors, parent, move)
    if (parent.isDefined)
      parent.get.addChild(move, newNode)
    newNode
  }

  def train(experience: ZeroExperienceBuffer): Unit = {
    val numExamples = experience.states.rows()

    val modelInput: INDArray = experience.states

    val visitSums = Nd4j.sum(experience.visitCounts, 1).reshape(Array[Int](numExamples, 1))

    val actionTarget = experience.visitCounts.div(visitSums)
    val valueTarget = experience.rewards

    model.fit(Array[INDArray](modelInput), Array[INDArray](actionTarget, valueTarget))

  }

}
