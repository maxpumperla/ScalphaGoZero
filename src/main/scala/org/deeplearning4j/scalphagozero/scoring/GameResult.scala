package org.deeplearning4j.scalphagozero.scoring

import org.deeplearning4j.scalphagozero.board.{ GameState, GoBoard, PlayerColor, Point }

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
  * Compute the result of a game
  *
  * @param blackPoints points black scored
  * @param whitePoints points white scored
  * @param komi the komi that was agreed to at the beginning of the game
  *
  * @author Max Pumperla
  */
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

  /**
    * Compute the game result from the current state.
    *
    * @param gameState GameState instance
    * @return GameResult object
    */
  def computeGameResult(gameState: GameState): GameResult = {
    val territory = evaluateTerritory(gameState.board)
    new GameResult(territory.numBlackTerritory + territory.numBlackStones,
                   territory.numWhiteStones + territory.numWhiteStones,
                   7.5)
  }

  /**
    * Evaluate / estimate the territory currently on
    * the Go board
    *
    * @param goBoard GoBoard instance
    * @return Territory object
    */
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
              fillWith = "territory_" + stoneString
            } else {
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

  private def collectRegion(startingPoint: Point,
                            board: GoBoard,
                            visited: ArrayBuffer[(Int, Int)] = ArrayBuffer()): (List[Point], Set[Option[Int]]) = {
    var visitedMap = visited
    val bool = visited.contains(startingPoint.toCoords)
    if (visited.contains(startingPoint.toCoords))
      return (List(), Set())

    var allPoints = List(startingPoint)
    var allBorders: Set[Option[Int]] = Set()
    visitedMap += startingPoint.toCoords
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
          allBorders += neighbor
        }
      }
    }
    (allPoints, allBorders)
  }

}
