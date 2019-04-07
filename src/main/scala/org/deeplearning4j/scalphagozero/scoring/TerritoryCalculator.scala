package org.deeplearning4j.scalphagozero.scoring

import org.deeplearning4j.scalphagozero.board.{ BlackPlayer, GoBoard, Player, Point }
import scala.collection.mutable.ListBuffer

/**
  *
  * @param goBoard the board to evaluate territory for
  * @author Max Pumperla
  * @author Barry Becker
  */
class TerritoryCalculator(goBoard: GoBoard) {

  /**
    * Evaluate / estimate the territory currently on the Go board
    *

    * @return Territory object
    */
  def evaluateTerritory(): Map[Point, VertexType] = {
    var statusMap = Map[Point, VertexType]()
    for (row <- 1 to goBoard.size; col <- 1 to goBoard.size) {
      val point = Point(row, col)
      if (!statusMap.contains(point)) {
        goBoard.getPlayer(point) match {
          case Some(color) =>
            statusMap += point -> (if (color == BlackPlayer) BlackStone else WhiteStone)
          case None =>
            val (group, neighbors) = collectRegion(point, goBoard)
            val fillWith =
              if (neighbors.size == 1) {
                val neighborColor: Player = neighbors.head
                if (neighborColor == BlackPlayer) BlackTerritory else WhiteTerritory
              } else {
                Dame
              }
            for (position <- group) {
              statusMap += position -> fillWith
            }
        }
      }
    }
    statusMap
  }

  private def collectRegion(startingPoint: Point, board: GoBoard): (List[Point], Set[Player]) = {
    val initialPlayer = board.getPlayer(startingPoint)

    var visitedPlayers = Set[Player]()
    val visitedPoints = ListBuffer[Point](startingPoint)

    var nextPoints = List[Point]()
    nextPoints :+= startingPoint

    while (nextPoints.nonEmpty) {
      val point = nextPoints.head
      nextPoints = nextPoints.tail
      val player = board.getPlayer(point)
      player.foreach(visitedPlayers += _)

      if (player == initialPlayer) {
        val nextVisits = point.neighbors.filter(board.isOnGrid).diff(visitedPoints)
        nextPoints = nextVisits ++ nextPoints
        visitedPoints += point
      }
    }

    (visitedPoints.toList, visitedPlayers)
  }
}
