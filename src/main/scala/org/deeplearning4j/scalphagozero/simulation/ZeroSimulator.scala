/*
 * Copyright 2016 Skymind
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.deeplearning4j.scalphagozero.simulation

import org.deeplearning4j.scalphagozero.agents.ZeroAgent
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
