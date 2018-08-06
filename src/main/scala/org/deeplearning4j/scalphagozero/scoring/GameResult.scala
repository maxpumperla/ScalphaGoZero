package org.deeplearning4j.scalphagozero.scoring

import org.deeplearning4j.scalphagozero.board.{GameState, GoBoard, PlayerColor, Point}

import scala.collection.mutable

class GameResult(blackPoints: Int, whitePoints: Int, komi: Double) {

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
            val (group, neighbors) = collectRegion(point, goBoard)
            var fillWith: String = ""
            if (neighbors.size == 1) {
              val neighborColor: Option[Int] = neighbors.head
              val stoneString = if (neighborColor.get == PlayerColor.black) "b" else "w"
              fillWith = "territory" + stoneString
            }
            else {
              fillWith = "dame"
            }
            for (position <- group) {
              statusMap.put(position, fillWith)
            }
          }
        }
      }
    }
    new Territory(statusMap)
  }

  private def collectRegion(startingPoint: Point, board: GoBoard, visited: Map[Point, Boolean] = Map()):
  (List[Point], Set[Option[Int]]) = {
    var visitedMap = visited
    if (visited.contains(startingPoint))
      return (List(), Set())

    var allPoints = List(startingPoint)
    var allBorders: Set[Option[Int]] = Set()
    visitedMap += (startingPoint -> true)
    val here: Option[Int] = board.getColor(startingPoint)
    val deltas = List((-1, 0), (1, 0), (0, -1), (0, 1))
    for ((row, col) <- deltas) {
      val nextPoint = Point(startingPoint.row + row, startingPoint.col + col)
      if (board.isOnGrid(nextPoint)) {
        val neighbor: Option[Int] = board.getColor(nextPoint)
        if (neighbor.equals(here)) {
          val (points, borders) = collectRegion(nextPoint, board, visitedMap)
          allPoints ++= points
          allBorders ++= borders
        } else {
          allBorders ++= neighbor
        }
      }
    }
    (allPoints, allBorders)
  }

  def computeGameResult(gameState: GameState): GameResult = {
    val territory = evaluateTerritory(gameState.board)
    new GameResult(
      territory.numBlackTerritory + territory.numBlackStones,
      territory.numWhi teStones + territory.numWhiteStones,
      7.5)
  }
}
