package org.deeplearning4j.scalphagozero.board

import java.util

import scala.collection.mutable

/**
  * Main Go board class, represents the board on which Go moves can be played.
  *
  * @author Max Pumperla
  */
class GoBoard(val row: Int, val col: Int) {

  private var grid: mutable.Map[Point, GoString] = mutable.Map.empty
  private var hash: Long = 0L

  if (!GoBoard.neighborTables.keySet.contains((row, col)))
    GoBoard.initNeighborTable(row, col)
  if (!GoBoard.cornerTables.keySet.contains((row, col)))
    GoBoard.initCornerTable(row, col)

  private var neighborMap: mutable.Map[Point, List[Point]] =
    GoBoard.neighborTables.getOrElse((row, col), mutable.Map.empty)
  private var cornerMap:  mutable.Map[Point, List[Point]] =
    GoBoard.cornerTables.getOrElse((row, col), mutable.Map.empty)

  def neighbors(point: Point): List[Point] = neighborMap.getOrElse(point, List())

  def corners(point: Point): List[Point] = cornerMap.getOrElse(point, List())

  def placeStone(player: Player, point: Point): Unit = {
    assert(isOnGrid(point))
    if (grid.get(point).isDefined)
      throw new IllegalStateException("Illegal play on point" + point.toString)
    assert(grid.get(point).isEmpty)

    // 1. Examine adjacent points
    val adjacentSameColor = new util.ArrayList[GoString]()
    val adjacentOppositeColor = new util.ArrayList[GoString]()
    val liberties = new util.ArrayList[Point]()

    for (neighbor: Point <- neighborMap(point)) {
      val neighborString = grid.get(neighbor)
      if (neighborString.isEmpty)
        liberties.add(neighbor)
      else if (neighborString.get.color == player.color) {
        if (!adjacentSameColor.contains(neighborString.get))
          adjacentSameColor.add(neighborString.get)
      } else {
        if (!adjacentOppositeColor.contains(neighborString.get))
          adjacentOppositeColor.add(neighborString.get)
      }
    }
    var newString = GoString(player.color, Set(point), liberties.toArray().toSet[Point])

    // 2. Merge any adjacent strings of the same color
    for (sameColorString <- adjacentSameColor)
      newString = newString.mergedWith(sameColorString)
    for (newStringPoint <- newString.stones)
      grid.put(newStringPoint, newString)
    hash ^= ZobristHashing.ZOBRIST((point, None)) // Remove empty-point hash code
    hash ^= ZobristHashing.ZOBRIST((point, Some(player.color))) // Add filled point hash code.


    // 3. Reduce liberties of any adjacent strings of the opposite color.
    // 4. If any opposite color strings now have zero liberties, remove them.
    for (otherColorString: GoString <- adjacentOppositeColor){
      val replacement = otherColorString.withoutLiberty(point)
      if (replacement.numLiberties > 0)
        this.replaceString(otherColorString.withoutLiberty(point))
      else
        this.removeString(otherColorString)
    }
  }

  private def removeString(goString: GoString): Unit =
    for (point <- goString.stones) {
      // Removing a string can create liberties for other strings.
      for (neighbor <- neighborMap(point) if grid.get(neighbor).isDefined) {
        val neighborString = grid.get(neighbor)
        if (neighborString.get.equals(goString))
          this.replaceString(neighborString.get.withLiberty(point))
        grid.remove(point)
      }
      hash ^= ZobristHashing.ZOBRIST((point, Some(goString.color))) //Remove filled point hash code.
      hash ^= ZobristHashing.ZOBRIST((point, None)) //Add empty point hash code.
    }


  private def replaceString(newString: GoString): Unit =
    for (point <- newString.stones)
      grid += (point -> newString)

  def isSelfCapture(player: Player, point: Point): Boolean = {
    val friendlyStrings: util.ArrayList[GoString] = new util.ArrayList[GoString]()
    for (neighbor <- neighborMap(point)) {
      val neighborString = grid.get(neighbor)
      if (neighborString.isEmpty)
        return false
      else if (neighborString.get.color == player.color)
        friendlyStrings.add(neighborString.get)
      else
        if (neighborString.get.numLiberties == 1)
          return false
    }
    var allNeighborsInDanger = true
    for (neighbor: GoString <- friendlyStrings)
      if (neighbor.numLiberties != 1) allNeighborsInDanger = false
    if (allNeighborsInDanger) return true
    false
  }

  def willCapture(player: Player, point: Point): Boolean = {
    for (neighbor <- neighborMap(point); if grid.get(neighbor).isDefined && grid(neighbor).color != player.color) {
      val neighborString = grid(neighbor)
      if (neighborString.numLiberties == 1)
        return true
    }
    false
  }

  def isOnGrid(point: Point): Boolean =  1 <= point.row && point.row <= row && 1 <= point.col && point.col <= col

  def getColor(point: Point): Option[Int] = grid.get(point).map(_.color)

  def getGoString(point: Point): Option[GoString] = grid.get(point)

  def equals(other: GoBoard): Boolean =
    this.row == other.row && this.col == other.col && this.grid.equals(other.grid)

  def zobristHash: Long = hash

  private def setBoardProperties(grid: mutable.Map[Point, GoString],
                                 hash: Long,
                                 neighborMap: mutable.Map[Point, List[Point]],
                                 cornerMap: mutable.Map[Point, List[Point]]): Unit = {
    this.hash = hash
    this.grid = grid
    this.neighborMap = neighborMap
    this.cornerMap = cornerMap
  }

  override def clone(): GoBoard = {
    val newBoard = new GoBoard(this.row, this.col)
    newBoard.setBoardProperties(this.grid, this.hash, this.neighborMap, this.cornerMap)
    newBoard
  }

}

object GoBoard {

  var neighborTables: mutable.Map[(Int, Int), mutable.Map[Point, List[Point]]] = mutable.Map.empty
  var cornerTables: mutable.Map[(Int, Int), mutable.Map[Point, List[Point]]] = mutable.Map.empty

  def initNeighborTable(row: Int, col: Int): Unit = {
    val neighborMap: mutable.Map[Point, List[Point]] = mutable.Map.empty
    for (r <- 1 to row) {
      for (c <- 1 to col) {
        val point = Point(row, col)
        val allNeighbors = point.neighbors
        val trueNeighbors =
          for (nb <- allNeighbors if 1 <= nb.row && nb.row <= row && 1 <= nb.col && nb.col <= col) yield nb
        neighborMap += (point -> trueNeighbors)
      }
    }
    neighborTables += ((row, col) -> neighborMap)
  }

  def initCornerTable(row: Int, col: Int): Unit = {
    val cornerMap: mutable.Map[Point, List[Point]] = mutable.Map.empty
    for (r <- 1 to row) {
      for (c <- 1 to col) {
        val point = Point(row, col)
        val allCorners = List(
          Point(row - 1, col - 1),
          Point(row + 1, col + 1),
          Point(row - 1, col + 1),
          Point(row + 1, col - 1)
        )
        val trueCorners =
          for (nb <- allCorners if 1 <= nb.row && nb.row <= row && 1 <= nb.col && nb.col <= col) yield nb
        cornerMap += (point -> trueCorners)
      }
    }
    cornerTables += ((row, col) -> cornerMap)
  }

}
