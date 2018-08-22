package org.deeplearning4j.scalphagozero.agents

import org.deeplearning4j.scalphagozero.board.{ GameState, Move }

/**
  * Go-playing agent. Knows how to select the next move.
  *
  * @author Max Pumperla
  */
trait Agent {

  /**
    * Select the next move.
    *
    * @param gameState current game state
    * @return predicted next move
    */
  def selectMove(gameState: GameState): Move

}
