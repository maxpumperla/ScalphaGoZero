package org.deeplearning4j.scalphagozero.agents

import org.deeplearning4j.scalphagozero.board._
import org.deeplearning4j.scalphagozero.util.Input

/**
  * A human agent allows a real person to select the next move in a go game.
  *
  * @author Barry Becker
  */
class HumanAgent() extends Agent {

  val input = new Input()

  override def selectMove(gameState: GameState): Move =
    input.getMoveFromUser
}
