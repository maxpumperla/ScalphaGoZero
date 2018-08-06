package org.deeplearning4j.scalphagozero.simulation

import org.deeplearning4j.scalphagozero.agents.Agent
import org.deeplearning4j.scalphagozero.board.{GameState, PlayerColor}
import org.deeplearning4j.scalphagozero.experience.ExperienceCollector
import org.deeplearning4j.scalphagozero.scoring.GameResult
import org.nd4j.linalg.factory.Nd4j

object Simulator {

  def simulateGame(boardHeight: Int = 19, boardWidth: Int = 19,
                   blackAgent: Agent, blackCollector: ExperienceCollector,
                   whiteAgent: Agent, whiteCollector: ExperienceCollector): Unit = {

    var game = GameState.newGame(boardHeight, boardWidth)
    val agents = Map(PlayerColor.black -> blackAgent, PlayerColor.white -> whiteAgent)

    blackCollector.beginEpisode()
    whiteCollector.beginEpisode()

    while (!game.isOver) {
      val nextMove = agents(game.nextPlayer.color).selectMove(game)
      game = game.applyMove(nextMove)
    }

    val gameResult = GameResult.computeGameResult(game)
    if (gameResult.winner == PlayerColor.black) {
      blackCollector.completeEpisode(Nd4j.scalar(1))
      whiteCollector.completeEpisode(Nd4j.scalar(-1))
    } else {
      blackCollector.completeEpisode(Nd4j.scalar(-1))
      whiteCollector.completeEpisode(Nd4j.scalar(1))
    }
  }
}
