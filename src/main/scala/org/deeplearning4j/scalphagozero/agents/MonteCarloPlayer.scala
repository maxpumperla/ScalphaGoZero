package org.deeplearning4j.scalphagozero.agents

import org.deeplearning4j.scalphagozero.board.Move
import scala.util.Random
import org.deeplearning4j.scalphagozero.board.Move.Pass

case class MonteCarloPlayer(nodeCreator: ZeroTreeNodeCreator, rnd: Random) {

  /**
    * Plays stochastically to the end of the game to determine a winner.
    * Moves use the probability distribution determined by the model priors instead of being completely random.
    * @param node current state to play from
    * @return the value from the point of view of the current player
    */
  def valueFromMCPlayout(node: ZeroTreeNode): Double =
    if (node.gameState.isOver) {
      if (node.gameState.gameResult().get.winner == node.gameState.nextPlayer) 1.0 else -1.0
    } else {
      val move = selectMoveStochastically(node)
      val newState = node.gameState.applyMove(move)
      // The parent is None because the playout does not need to be part of the MCTree.
      val childNode = nodeCreator.createNode(newState, Some(move), None)
      -valueFromMCPlayout(childNode)
    }

  /**
    * Stochastic selection favoring moves with higher scores.
    * Don't select pass unless its the only option.
    */
  private def selectMoveStochastically(node: ZeroTreeNode): Move =
    if (node.branches.size <= 1) {
      Pass
    } else {
      val movesWithPriors = node.branches.toList.filter(m => m._1 != Pass)
      val priors = movesWithPriors.map(_._2.prior).toArray
      val idx = selectIdxFromDistribution(priors)
      movesWithPriors(idx)._1
    }

  /**
    * @return an index selected stochastically from a distribution defined by a.
    *         If all the values of a are 0, then the first is returned.
    */
  def selectIdxFromDistribution(a: Array[Double]): Int = {
    assert(a.nonEmpty)
    val sum = a.sum
    if (sum == 0) 0
    else {
      val r = rnd.nextDouble() * sum
      var t: Double = 0
      var ct = 0
      val len = a.length
      while (t <= r && ct < len) {
        t += a(ct)
        ct += 1
      }
      if (ct == 0) println("bad a distribution = " + a.mkString(", "))
      ct - 1
    }
  }
}
