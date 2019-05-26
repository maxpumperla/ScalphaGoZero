package org.deeplearning4j.scalphagozero.agents

import org.deeplearning4j.scalphagozero.board.Move.Play
import org.deeplearning4j.scalphagozero.board.{ GameState, Move }

import scala.collection.mutable.ArrayBuffer

/**
  * Use this in place of a real ZeroTreeNodeCreator to avoid creating a model and encoder
  */
class MockNodeCreator extends ZeroTreeNodeCreator(null, null) {

  override def createNode(
      gameState: GameState,
      move: Option[Move] = None,
      parent: Option[ZeroTreeNode] = None
  ): ZeroTreeNode = {

    val value = 0.5

    val validMoves = findValidMoves(gameState)
    val movePriors = validMoves.map(m => (m, 0.5)).toMap

    new ZeroTreeNode(gameState, value, movePriors, parent, move)
  }

  private def findValidMoves(state: GameState): Seq[Move] = {
    val abuf: ArrayBuffer[Move] = ArrayBuffer()
    val s = state.board.size
    for (i <- 1 to s)
      for (j <- 1 to s) {
        val m = Play(i, j)
        if (state.isValidMove(m)) {
          abuf.append(m)
        }
      }
    abuf
  }

}
