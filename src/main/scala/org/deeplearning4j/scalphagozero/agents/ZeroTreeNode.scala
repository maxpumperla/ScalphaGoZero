package org.deeplearning4j.scalphagozero.agents

import org.deeplearning4j.scalphagozero.board.{ GameState, Move }

import scala.collection.mutable

class ZeroTreeNode(val gameState: GameState,
                   val value: Double,
                   val priors: Map[Move, Double],
                   val parent: Option[ZeroTreeNode],
                   lastMove: Move) {

  var totalVisitCount: Int = 1
  var branches: mutable.Map[Move, Branch] = mutable.Map()
  var children: mutable.Map[Move, ZeroTreeNode] = mutable.Map()

  for ((move, prior) <- priors) {
    if (gameState.isValidMove(move)) {
      branches(move) = new Branch(prior)
    }
  }

  def moves: List[Move] = branches.keys.toList

  def addChild(move: Move, childNode: ZeroTreeNode): Unit = children += (move -> childNode)

  def hasChild(move: Move): Boolean = children.contains(move)

  def recordVisit(move: Move, value: Double): Unit = {
    totalVisitCount += 1
    val b = branches(move)
    val updatedBranch = new Branch(b.prior, b.visitCount + 1, b.totalValue + value)
    branches(move) = updatedBranch
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
