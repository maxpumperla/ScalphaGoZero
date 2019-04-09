package org.deeplearning4j.scalphagozero.agents

import org.deeplearning4j.scalphagozero.board.{ GameState, Move }

/**
  * Tree node of an AlphaGo Zero game tree.
  *
  * @param gameState current game state
  * @param value value of this node
  * @param priors Map of moves to prior values (estimated by policy network)
  * @param parent optional parent of this node
  * @param lastMove optional last played move
  */
class ZeroTreeNode(
    val gameState: GameState,
    val value: Double,
    val priors: Map[Move, Double],
    val parent: Option[ZeroTreeNode],
    val lastMove: Option[Move]
) {

  var totalVisitCount: Int = 1

  private var children: Map[Move, ZeroTreeNode] = Map()

  // Add child moves for all valid moves
  private var branches: Map[Move, Branch] =
    priors
      .foldLeft(Map[Move, Branch]()) {
        case (acc, (move, prior)) =>
          if (gameState.isValidMove(move)) acc + (move -> Branch(prior))
          else acc
      }

  def moves: List[Move] = branches.keys.toList

  def addChild(move: Move, childNode: ZeroTreeNode): Unit = children += (move -> childNode)

  def getChild(move: Move): Option[ZeroTreeNode] = children.get(move)

  def hasChild(move: Move): Boolean = children.contains(move)

  def recordVisit(move: Move, value: Double): Unit = {
    totalVisitCount += 1
    if (branches.contains(move)) {
      val b = branches(move)
      val updatedBranch = Branch(b.prior, b.visitCount + 1, b.totalValue + value)
      branches += (move -> updatedBranch)
    }
    ()
  }

  def expectedValue(move: Move): Double = {
    val branch = branches(move)
    branch.visitCount match {
      case 0 => 0.0
      case _ => branch.totalValue / branch.visitCount.toDouble
    }
  }

  def prior(move: Move): Double = branches(move).prior

  def visitCount(move: Move): Int = if (branches.contains(move)) branches(move).visitCount else 0
}
