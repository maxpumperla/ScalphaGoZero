package org.deeplearning4j.scalphagozero.scoring

import org.deeplearning4j.scalphagozero.board.{ GoBoard, Point, _ }

import scala.collection.mutable
import scala.collection.mutable.{ ArrayBuffer, ListBuffer }

/**
  * Can we find a better name ?
  */
sealed trait GamePointType extends Product with Serializable
case object BlackStone extends GamePointType
case object WhiteStone extends GamePointType
case object BlackTerritory extends GamePointType
case object WhiteTerritory extends GamePointType
case object Dame extends GamePointType

/**
  * Compute the result of a game
  *
  * @author Max Pumperla
  */
final case class GameResult(
    numBlackStones: Int,
    numWhiteStones: Int,
    numBlackTerritory: Int,
    numWhiteTerritory: Int,
    numDame: Int,
    komi: Double
) {

  /**
    * Points black scored
    */
  val blackPoints: Int = numBlackTerritory + numBlackStones

  /**
    * points white scored
    */
  val whitePoints: Int = numWhiteStones + numWhiteStones

  val winner: Player = if (blackPoints > whitePoints + komi) BlackPlayer else WhitePlayer

  val winningMargin: Double = Math.abs(blackPoints - (whitePoints + komi))

  override lazy val toString: String = {
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
    * @param goBoard GoBoard instance
    * @return GameResult object
    */
  def computeGameResult(goBoard: GoBoard): GameResult = {
    val territoryMap = evaluateTerritory(goBoard)

    var numBlackStones = 0
    var numWhiteStones = 0
    var numBlackTerritory = 0
    var numWhiteTerritory = 0
    var numDame = 0

    for ((_, status) <- territoryMap) {
      status match {
        case BlackStone     => numBlackStones += 1
        case WhiteStone     => numWhiteStones += 1
        case BlackTerritory => numBlackTerritory += 1
        case WhiteTerritory => numWhiteTerritory += 1
        case Dame           => numDame += 1
      }
    }

    GameResult(
      numBlackStones = numBlackStones,
      numWhiteStones = numWhiteStones,
      numBlackTerritory = numBlackTerritory,
      numWhiteTerritory = numWhiteTerritory,
      numDame = numDame,
      komi = 7.5
    )
  }

  /**
    * Evaluate / estimate the territory currently on
    * the Go board
    *
    * @param goBoard GoBoard instance
    * @return Territory object
    */
  def evaluateTerritory(goBoard: GoBoard): Map[Point, GamePointType] = {
    val statusMap = mutable.Map.empty[Point, GamePointType]
    for (row <- 1 to goBoard.row; col <- 1 to goBoard.col) {
      val point = Point(row, col)
      if (!statusMap.contains(point)) {
        goBoard.getPlayer(point) match {
          case Some(color) =>
            statusMap.put(point, if (color == BlackPlayer) BlackStone else WhiteStone)
          case None =>
            val (group, neighbors) = collectRegion(point, goBoard)
            val fillWith =
              if (neighbors.size == 1) {
                val neighborColor: Option[Player] = neighbors.head
                if (neighborColor.get == BlackPlayer) BlackTerritory else WhiteTerritory
              } else {
                Dame
              }
            for (position <- group) {
              statusMap.put(position, fillWith)
            }
        }
      }
    }
    statusMap.toMap
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
