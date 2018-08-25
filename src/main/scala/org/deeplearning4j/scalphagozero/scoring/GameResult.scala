package org.deeplearning4j.scalphagozero.scoring

import org.deeplearning4j.scalphagozero.board.PlayerColor.{ Black, White }
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
final case class GameResult(blackPoints: Int, whitePoints: Int, komi: Double) {

  val winner: PlayerColor = if (blackPoints > whitePoints + komi) PlayerColor.Black else PlayerColor.White

  val winningMargin: Double = Math.abs(blackPoints - (whitePoints + komi))

  override def toString: String = {
    val white = whitePoints + komi
    winner match {
      case Black => "B+ " + (blackPoints - white)
      case White => "W+ " + (white - blackPoints)
    }
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
    new GameResult(
      territory.numBlackTerritory + territory.numBlackStones,
      territory.numWhiteStones + territory.numWhiteStones,
      7.5
    )
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
          val stoneColor: Option[PlayerColor] = goBoard.getColor(point)
          if (stoneColor.isDefined) {
            val color = stoneColor.get
            val status = if (color == PlayerColor.Black) "black" else "white"
            statusMap.put(point, status)
          } else {
            val (group, neighbors) = collectRegion(point, goBoard)
            var fillWith: String = ""
            if (neighbors.size == 1) {
              val neighborColor: Option[PlayerColor] = neighbors.head
              val stoneString = if (neighborColor.get == PlayerColor.Black) "b" else "w"
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

  private def collectRegion(
      startingPoint: Point,
      board: GoBoard,
      visited: ArrayBuffer[(Int, Int)] = ArrayBuffer()
  ): (List[Point], Set[Option[PlayerColor]]) = {
    var visitedMap = visited
    if (visited.contains(startingPoint.toCoords))
      return (List(), Set())

    var allPoints = List(startingPoint)
    var allBorders: Set[Option[PlayerColor]] = Set()
    visitedMap += startingPoint.toCoords
    val here: Option[PlayerColor] = board.getColor(startingPoint)
    val deltas = List((-1, 0), (1, 0), (0, -1), (0, 1))
    for ((row, col) <- deltas) {
      val nextPoint = Point(startingPoint.row + row, startingPoint.col + col)
      if (board.isOnGrid(nextPoint)) {
        val neighbor: Option[PlayerColor] = board.getColor(nextPoint)
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
