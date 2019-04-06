package org.deeplearning4j.scalphagozero.simulation

import org.deeplearning4j.scalphagozero.agents.ZeroAgent
import org.deeplearning4j.scalphagozero.board._
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
    val boardSize = encoder.boardSize

    val blackCollector = blackAgent.collector
    val whiteCollector = whiteAgent.collector

    var game = GameState.newGame(boardSize)
    val agents: Map[Player, ZeroAgent] = Map(BlackPlayer -> blackAgent, WhitePlayer -> whiteAgent)

    blackCollector.beginEpisode()
    whiteCollector.beginEpisode()

    game = doSimulation(game, agents)

    val gameResult = GameResult.computeGameResult(game.board)
    gameResult.winner match {
      case BlackPlayer =>
        blackCollector.completeEpisode(Nd4j.scalar(1))
        whiteCollector.completeEpisode(Nd4j.scalar(-1))
      case WhitePlayer =>
        blackCollector.completeEpisode(Nd4j.scalar(-1))
        whiteCollector.completeEpisode(Nd4j.scalar(1))
    }
  }

  private def doSimulation(initialState: GameState, agents: Map[Player, ZeroAgent]): GameState = {
    println(">>> Starting a new game.")
    println("Initial board:")
    var game = initialState
    println(game.board)
    while (!game.isOver) {
      val nextMove = agents(game.nextPlayer).selectMove(game)

      if (game.isValidMove(nextMove)) {
        println(game.nextPlayer + " " + nextMove.toString)
        game = game.applyMove(nextMove)
        println(game.board)
      } else {
        println(game.nextPlayer + " now resigns.")
        game = game.applyMove(Move.Resign)
        println("The final game state is:\n" + game.board)
      }
    }
    println(">>> Simulation finished.")
    println()
    game
  }
}
