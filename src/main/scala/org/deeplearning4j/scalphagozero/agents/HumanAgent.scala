package org.deeplearning4j.scalphagozero.agents

import org.deeplearning4j.scalphagozero.board._
import org.deeplearning4j.scalphagozero.input.Input

/**
  * A human agent allows a real person to select the next move in a go game.
  *
  * @author Barry Becker
  */
class HumanAgent() extends Agent {

  private val input = Input()

  override def selectMove(gameState: GameState): Move =
    input.getMoveFromUser(gameState.board.size)
}
