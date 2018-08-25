package org.deeplearning4j.scalphagozero.scoring

import org.deeplearning4j.scalphagozero.board._

import scala.collection.mutable
import scala.collection.mutable.{ ArrayBuffer, ListBuffer }

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

  val winner: Player = if (blackPoints > whitePoints + komi) BlackPlayer else WhitePlayer

  val winningMargin: Double = Math.abs(blackPoints - (whitePoints + komi))

  override def toString: String = {
    val white = whitePoints + komi
    winner match {
      case BlackPlayer => "B+ " + (blackPoints - white)
      case WhitePlayer => "W+ " + (white - blackPoints)
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
          val stoneColor: Option[Player] = goBoard.getPlayer(point)
          if (stoneColor.isDefined) {
            val color = stoneColor.get
            val status = if (color == BlackPlayer) "black" else "white"
            statusMap.put(point, status)
          } else {
            val (group, neighbors) = collectRegion(point, goBoard)
            var fillWith: String = ""
            if (neighbors.size == 1) {
              val neighborColor: Option[Player] = neighbors.head
              val stoneString = if (neighborColor.get == BlackPlayer) "b" else "w"
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
  ): (List[Point], Set[Option[Player]]) = {
    if (visited.contains(startingPoint.toCoords))
      return (List.empty, Set.empty)

    val allPoints = ListBuffer(startingPoint)
    val allBorders: mutable.Set[Option[Player]] = mutable.Set.empty
    visited += startingPoint.toCoords
    val here: Option[Player] = board.getPlayer(startingPoint)
    val deltas = List((-1, 0), (1, 0), (0, -1), (0, 1))
    for ((row, col) <- deltas) {
      val nextPoint = Point(startingPoint.row + row, startingPoint.col + col)
      if (board.isOnGrid(nextPoint)) {
        val neighbor: Option[Player] = board.getPlayer(nextPoint)
        if (neighbor.equals(here)) {
          val (points, borders) = collectRegion(nextPoint, board, visited)
          allPoints ++= points
          allBorders ++= borders
        } else {
          allBorders += neighbor
        }
      }
    }
    (allPoints.toList, allBorders.toSet)
  }

}
