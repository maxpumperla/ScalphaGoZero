package org.deeplearning4j.scalphagozero.scoring

import org.deeplearning4j.scalphagozero.board.{ GameState, GoBoard, PlayerColor }

class GameResult(blackPoints: Int, whitePoints: Int, komi: Int) {

  def winner: Int = if (blackPoints > whitePoints + komi) PlayerColor.black else PlayerColor.white

  def winningMargin: Double = Math.abs(blackPoints - (whitePoints + komi))

  override def toString: String = {
    val white = whitePoints + komi
    if (winner == PlayerColor.black)
      "B+ " + (blackPoints - white)
    else
      "W+ " + (white - blackPoints)
  }
}

object GameResult {

  def evaluateTerritory(goBoard: GoBoard): Territory = new Territory // TODO

  //private def collectRegion(startPos, board, visited=None) TODO

  def computeGameResult(gameState: GameState): GameResult = new GameResult(0, 0, 0) // TODO
}
