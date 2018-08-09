package org.deeplearning4j.scalphagozero.simulation

import org.deeplearning4j.scalphagozero.agents.ZeroAgent
import org.deeplearning4j.scalphagozero.board.{GameState, Move, PlayerColor}
import org.deeplearning4j.scalphagozero.scoring.GameResult
import org.nd4j.linalg.factory.Nd4j

object ZeroSimulator {

  def simulateGame(blackAgent: ZeroAgent,
                   whiteAgent: ZeroAgent): Unit = {

    val encoder = blackAgent.encoder
    val boardHeight = encoder.boardHeight
    val boardWidth = encoder.boardWidth

    val blackCollector = blackAgent.getCollector
    val whiteCollector = whiteAgent.getCollector

    var game = GameState.newGame(boardHeight, boardWidth)
    val agents = Map(PlayerColor.black -> blackAgent, PlayerColor.white -> whiteAgent)

    blackCollector.beginEpisode()
    whiteCollector.beginEpisode()

    while (!game.isOver) {
      val nextMove = agents(game.nextPlayer.color).selectMove(game)
      if (game.isValidMove(nextMove)) // TODO this shouldn't happen, just for testing
        game = game.applyMove(nextMove)
      else
        game = game.applyMove(Move.resign())
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
