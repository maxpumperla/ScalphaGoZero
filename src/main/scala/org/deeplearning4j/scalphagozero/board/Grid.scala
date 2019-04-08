package org.deeplearning4j.scalphagozero.board

import ZobristHashing.ZOBRIST

/**
  * Keeps track of parent string for each location on the board where there is a stone.
  *
  * @param grid map from grid point location to parent string of stones (if any)
  * @param hash the Zobrist hash. Gets updated as moves are played. Used to detect ko.
  * @author Max Pumperla
  * @author Barry Becker
  */
case class Grid(grid: Map[Point, GoString] = Map.empty, hash: Long = 0L) {

  def getString(point: Point): Option[GoString] = grid.get(point)
  def getPlayer(point: Point): Option[Player] = grid.get(point).map(_.player)

  /**
    * @param point the position of the stone that was added
    * @param newString the new parent string for that added stone
    */
  def updateStringWhenAddingStone(point: Point, newString: GoString): Grid = {
    var newGrid = grid
    for (newStringPoint: Point <- newString.stones)
      newGrid += newStringPoint -> newString

    var newHash = hash
    newHash ^= ZOBRIST((point, None)) // Remove empty-point hash code
    newHash ^= ZOBRIST((point, Some(newString.player))) // Add filled point hash code.

    Grid(newGrid, newHash)
  }

  def replaceString(newString: GoString): Grid = Grid(replaceString(newString, grid), hash)

  private def replaceString(newString: GoString, g: Map[Point, GoString]): Map[Point, GoString] = {
    var newGrid = g
    for (point <- newString.stones)
      newGrid += (point -> newString)
    newGrid
  }

  /**
    * When a string is removed due to capture, also update the liberties of the adjacent strings of opposite color.
    * @param goString the string to remove
    * @return newGrid and newHash value
    */
  def removeString(goString: GoString, nbrMap: Map[Point, List[Point]]): Grid = {
    var newGrid = grid
    var newHash = hash

    // first remove the stones from the board
    goString.stones.foreach { point =>
      newGrid -= point // the point is now empty
      newHash ^= ZOBRIST((point, Some(goString.player))) //Remove filled point hash code.
      newHash ^= ZOBRIST((point, None)) //Add empty point hash code.
    }

    // for each opponent neighbor string add a liberty for each adjacent removed point
    goString.stones.foreach { point =>
      nbrMap(point).foreach { neighbor =>
        val oppNbrString = newGrid.get(neighbor)
        if (oppNbrString.nonEmpty) {
          newGrid = replaceString(oppNbrString.get.withLiberty(point), newGrid)
        }
      }
    }
    Grid(newGrid, newHash)
  }
}
