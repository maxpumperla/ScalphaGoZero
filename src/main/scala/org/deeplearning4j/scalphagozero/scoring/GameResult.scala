package org.deeplearning4j.scalphagozero.scoring

import org.deeplearning4j.scalphagozero.board.{GameState, GoBoard, PlayerColor, Point}

import scala.collection.mutable

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

  def evaluateTerritory(goBoard: GoBoard): Territory = {
    val statusMap = new mutable.HashMap[Point, String]()
    for (row <- 1 to goBoard.row) {
      for (col <- 1 to goBoard.col) {
        val point = Point(row, col)
        if (!statusMap.contains(point)) {
          val stoneColor: Option[Int] = goBoard.getColor(point)
          if (stoneColor.isDefined) {
            val color = stoneColor.get
            val status = if (color == PlayerColor.black) "black" else "white"
            statusMap.put(point, status)
          } else {
            //TODO
          }
        }
      }
    }
  }

  //private def collectRegion(startPos, board, visited=None) TODO

  def computeGameResult(gameState: GameState): GameResult = new GameResult(0, 0, 0) // TODO
}
