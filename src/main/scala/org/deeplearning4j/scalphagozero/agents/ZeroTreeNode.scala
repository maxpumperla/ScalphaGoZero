package org.deeplearning4j.scalphagozero.agents

import org.deeplearning4j.scalphagozero.board.{ GameState, Move }

import scala.collection.mutable

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
    val priors: mutable.Map[Move, Double],
    val parent: Option[ZeroTreeNode],
    val lastMove: Option[Move]
) {

  var totalVisitCount: Int = 1
  var branches: mutable.Map[Move, Branch] = mutable.Map()
  var children: mutable.Map[Move, ZeroTreeNode] = mutable.Map()

  for ((move, prior) <- priors) {
    if (gameState.isValidMove(move)) {
      branches.put(move, Branch(prior))
    }
  }

  def moves: List[Move] = branches.keys.toList

  def addChild(move: Move, childNode: ZeroTreeNode): Unit = children += (move -> childNode)

  def getChild(move: Move): Option[ZeroTreeNode] = children.get(move)

  def hasChild(move: Move): Boolean = children.contains(move)

  def recordVisit(move: Move, value: Double): Unit = {
    totalVisitCount += 1
    val b = branches(move)
    val updatedBranch = Branch(b.prior, b.visitCount + 1, b.totalValue + value)
    branches.put(move, updatedBranch)
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
