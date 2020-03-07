package org.deeplearning4j.scalphagozero.scoring

import org.deeplearning4j.scalphagozero.board.{ BlackPlayer, GoBoard, Player, Point }
import scala.collection.mutable.ListBuffer

/**
  * @param goBoard the board to evaluate territory for
  * @author Max Pumperla
  * @author Barry Becker
  */
class TerritoryCalculator(goBoard: GoBoard) {

  /**
    * Evaluate / estimate the territory currently on the Go board
    * Stones that belong to a string that is in atari, are considered dead and captured.
    * The reason this can happen is that the computer will always play valid moves until there are no more valid moves.
    * Since suicide is not legal, it will play until a group is in atari, then stop.
    * A human may stop playing before that happen, so at the time when both players pass, there may be
    * some stones in atari. Those stones need to be removed and counted as captures.
    *
    * @return Territory object
    */
  def evaluateTerritory(): Map[Point, VertexType] = {

    // Make 2 passes. In first pass, categorize all stones
    val statusMap = categorizeStones()
    // In second pass, mark all the empty regions, and dead stones, as belonging to a players territory.
    categorizeTerritory(statusMap)
  }

  /**
    * First add all the stones stones ont the board to the map with a proper categorization.
    * i.e. either live stone or captured stone. A stone is considered captured if part of an a string in atari.
    */
  private def categorizeStones(): Map[Point, VertexType] = {
    var statusMap = Map[Point, VertexType]()

    for (row <- 1 to goBoard.size; col <- 1 to goBoard.size) {
      val point = Point(row, col)
      val playerOption = goBoard.getPlayer(point)
      if (playerOption.nonEmpty) {
        val isBlack = playerOption.get == BlackPlayer
        if (goBoard.getGoString(point).get.numLiberties == 1)
          statusMap += point -> (if (isBlack) CapturedBlackStone else CapturedWhiteStone)
        else
          statusMap += point -> (if (isBlack) BlackStone else WhiteStone)
      }
    }
    statusMap
  }

  private def categorizeTerritory(pointToType: Map[Point, VertexType]): Map[Point, VertexType] = {
    var statusMap = pointToType

    for (row <- 1 to goBoard.size; col <- 1 to goBoard.size) {
      val point = Point(row, col)
      val playerOption = goBoard.getPlayer(point)
      if (playerOption.isEmpty || statusMap(point).isTerritory) {
        val (group, neighbors) = collectRegion(point, goBoard, statusMap)
        val fillWith =
          if (neighbors.size == 1) { // then all one color neighbors
            val neighborColor: Player = neighbors.head
            if (neighborColor == BlackPlayer) BlackTerritory else WhiteTerritory
          } else Dame
        group.filter(p => !statusMap.contains(p)).foreach(pos => statusMap += pos -> fillWith)
      }
    }
    statusMap
  }

  /**
    * @return (<list of points in the unoccupied area seeded by startingPint>,
    *          <players that own stones adjacent to this area>)
    *         If the region is bordered by only one player, then it is considered to be territory for that player.
    */
  private def collectRegion(
      startingPoint: Point,
      board: GoBoard,
      statusMap: Map[Point, VertexType]
  ): (List[Point], Set[Player]) = {
    val initialPlayer = board.getPlayer(startingPoint)
    assert(initialPlayer.isEmpty || statusMap(startingPoint).isTerritory)

    var visitedPlayers = Set[Player]()
    val visitedPoints = ListBuffer[Point](startingPoint)

    var nextPoints = List[Point]()
    nextPoints :+= startingPoint

    while (nextPoints.nonEmpty) {
      val point = nextPoints.head
      nextPoints = nextPoints.tail
      val player = board.getPlayer(point)
      if (player.nonEmpty && !statusMap(point).isTerritory)
        visitedPlayers += player.get

      if (player.isEmpty || statusMap(point).isTerritory) {
        val nextVisits = point.neighbors.filter(board.isOnGrid).diff(visitedPoints)
        nextPoints = nextVisits ++ nextPoints
        visitedPoints += point
      }
    }

    (visitedPoints.toList, visitedPlayers)
  }
}
