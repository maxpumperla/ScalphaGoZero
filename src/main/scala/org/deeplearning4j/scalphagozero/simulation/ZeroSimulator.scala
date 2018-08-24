package org.deeplearning4j.scalphagozero.simulation

import org.deeplearning4j.scalphagozero.agents.ZeroAgent
import org.deeplearning4j.scalphagozero.board.PlayerColor.{ Black, White }
import org.deeplearning4j.scalphagozero.board.{ GameState, Move, PlayerColor }
import org.deeplearning4j.scalphagozero.scoring.GameResult
import org.nd4j.linalg.factory.Nd4j

/**
  * Simulate a game between two AlphaGo Zero agents.
  *
  * @author Max Pumperla
  */
object ZeroSimulator {

  def simulateGame(blackAgent: ZeroAgent, whiteAgent: ZeroAgent): Unit = {

    val encoder = blackAgent.encoder
    val boardHeight = encoder.boardHeight
    val boardWidth = encoder.boardWidth

    val blackCollector = blackAgent.collector
    val whiteCollector = whiteAgent.collector

    var game = GameState.newGame(boardHeight, boardWidth)
    val agents = Map(PlayerColor.Black -> blackAgent, PlayerColor.White -> whiteAgent)

    blackCollector.beginEpisode()
    whiteCollector.beginEpisode()

    println(">>> Starting a new game.")
    while (!game.isOver) {
      val nextMove = agents(game.nextPlayer.color).selectMove(game)
      game = if (game.isValidMove(nextMove)) game.applyMove(nextMove) else game.applyMove(Move.Resign)
    }
    println(">>> Simulation terminated.")

    val gameResult = GameResult.computeGameResult(game)
    gameResult.winner match {
      case Black =>
        blackCollector.completeEpisode(Nd4j.scalar(1))
        whiteCollector.completeEpisode(Nd4j.scalar(-1))
      case White =>
        blackCollector.completeEpisode(Nd4j.scalar(-1))
        whiteCollector.completeEpisode(Nd4j.scalar(1))
    }
  }
}
