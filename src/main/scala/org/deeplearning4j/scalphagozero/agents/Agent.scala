package org.deeplearning4j.scalphagozero.agents

import org.deeplearning4j.scalphagozero.board.{ GameState, Move }

/**
  * Go-playing agent. Knows how to select the next move.
  *
  * @author Max Pumperla
  */
trait Agent {

  def selectMove(gameState: GameState): Move

}
