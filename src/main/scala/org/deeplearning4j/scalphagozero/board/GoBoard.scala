package org.deeplearning4j.scalphagozero.board

import java.util

import scala.collection.mutable
import scala.collection.JavaConversions._
import scala.collection.JavaConverters._

/**
  * Main Go board class, represents the board on which Go moves can be played.
  *
  * @author Max Pumperla
  */
class GoBoard(val row: Int, val col: Int) {

  private var grid: mutable.Map[(Int, Int), GoString] = mutable.Map.empty
  private var hash: Long = 0L

  if (!GoBoard.neighborTables.keySet.contains((row, col)))
    GoBoard.initNeighborTable(row, col)
  if (!GoBoard.cornerTables.keySet.contains((row, col)))
    GoBoard.initCornerTable(row, col)

  private var neighborMap: mutable.Map[(Int, Int), List[Point]] =
    GoBoard.neighborTables.getOrElse((row, col), mutable.Map.empty)
  private var cornerMap: mutable.Map[(Int, Int), List[Point]] =
    GoBoard.cornerTables.getOrElse((row, col), mutable.Map.empty)

  def neighbors(point: Point): List[Point] = neighborMap.getOrElse((point.row, point.col), List())

  def corners(point: Point): List[Point] = cornerMap.getOrElse((point.row, point.col), List())

  def placeStone(player: Player, point: Point): Unit = {

    assert(isOnGrid(point))
    if (grid.get(point.toCoords).isDefined)
      throw new IllegalStateException("Illegal play on point" + point.toString)
    assert(grid.get(point.toCoords).isEmpty)

    // 1. Examine adjacent points
    val adjacentSameColor = new util.ArrayList[GoString]()
    val adjacentOppositeColor = new util.ArrayList[GoString]()
    val liberties: util.ArrayList[(Int, Int)] = new util.ArrayList()

    for (neighbor: Point <- neighborMap((point.row, point.col))) {
      val neighborString = grid.get(neighbor.toCoords)
      if (neighborString.isEmpty)
        liberties.add(neighbor.toCoords)
      else if (neighborString.get.color == player.color) {
        if (!adjacentSameColor.contains(neighborString.get))
          adjacentSameColor.add(neighborString.get)
      } else {
        if (!adjacentOppositeColor.contains(neighborString.get))
          adjacentOppositeColor.add(neighborString.get)
      }
    }
    val libertySet: Array[(Int, Int)] = liberties.asScala.toArray[(Int, Int)]
    var newString = GoString(player.color, Set(point.toCoords), Set(libertySet: _*))

    // 2. Merge any adjacent strings of the same color
    for (sameColorString: GoString <- adjacentSameColor)
      newString = newString.mergedWith(sameColorString)
    for (newStringPoint: (Int, Int) <- newString.stones)
      grid.put(newStringPoint, newString)
    hash ^= ZobristHashing.ZOBRIST(((point.row, point.col), None)) // Remove empty-point hash code
    hash ^= ZobristHashing.ZOBRIST(((point.row, point.col), Some(player.color))) // Add filled point hash code.

    // 3. Reduce liberties of any adjacent strings of the opposite color.
    // 4. If any opposite color strings now have zero liberties, remove them.
    for (otherColorString: GoString <- adjacentOppositeColor) {
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
      for (neighbor <- neighborMap((point._1, point._1)) if grid.get(neighbor.toCoords).isDefined) {
        val neighborString = grid.get(neighbor.toCoords)
        if (neighborString.get.equals(goString))
          this.replaceString(neighborString.get.withLiberty(Point(point._1, point._2)))
        grid.remove(point)
      }
      hash ^= ZobristHashing.ZOBRIST(((point._1, point._2), Some(goString.color))) //Remove filled point hash code.
      hash ^= ZobristHashing.ZOBRIST(((point._1, point._2), None)) //Add empty point hash code.
    }

  private def replaceString(newString: GoString): Unit =
    for (point <- newString.stones)
      grid += (point -> newString)

  def isSelfCapture(player: Player, point: Point): Boolean = {
    val friendlyStrings: util.ArrayList[GoString] = new util.ArrayList[GoString]()
    for (neighbor <- neighborMap((point.row, point.col))) {
      val neighborString = grid.get(neighbor.toCoords)
      if (neighborString.isEmpty)
        return false
      else if (neighborString.get.color == player.color)
        friendlyStrings.add(neighborString.get)
      else if (neighborString.get.numLiberties == 1)
        return false
    }
    var allNeighborsInDanger = true
    for (neighbor: GoString <- friendlyStrings)
      if (neighbor.numLiberties != 1) allNeighborsInDanger = false
    if (allNeighborsInDanger) return true
    false
  }

  def willCapture(player: Player, point: Point): Boolean = {
    for (neighbor <- neighborMap((point.row, point.col))
         if grid.get(neighbor.toCoords).isDefined && grid(neighbor.toCoords).color != player.color) {
      val neighborString = grid(neighbor.toCoords)
      if (neighborString.numLiberties == 1)
        return true
    }
    false
  }

  def isOnGrid(point: Point): Boolean = 1 <= point.row && point.row <= row && 1 <= point.col && point.col <= col

  def getColor(point: Point): Option[PlayerColor] = grid.get(point.toCoords).map(_.color)

  def getGoString(point: Point): Option[GoString] = grid.get(point.toCoords)

  override def equals(obj: scala.Any): Boolean = {
    obj match {
      case other: GoBoard =>
        return this.row == other.row && this.col == other.col && this.grid.equals(other.grid)
      case _ =>
    }
    false
  }

  def zobristHash: Long = hash

  private def setBoardProperties(
      grid: mutable.Map[(Int, Int), GoString],
      hash: Long,
      neighborMap: mutable.Map[(Int, Int), List[Point]],
      cornerMap: mutable.Map[(Int, Int), List[Point]]
  ): Unit = {
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

  def apply(boardHeight: Int, boardWidth: Int): GoBoard = new GoBoard(boardHeight, boardWidth)

  var neighborTables: mutable.Map[(Int, Int), mutable.Map[(Int, Int), List[Point]]] = mutable.Map.empty
  var cornerTables: mutable.Map[(Int, Int), mutable.Map[(Int, Int), List[Point]]] = mutable.Map.empty

  def initNeighborTable(row: Int, col: Int): Unit = {
    val neighborMap: mutable.Map[(Int, Int), List[Point]] = mutable.Map.empty
    for (r <- 1 to row) {
      for (c <- 1 to col) {
        val point = Point(r, c)
        val allNeighbors = point.neighbors
        val trueNeighbors =
          for (nb <- allNeighbors if 1 <= nb.row && nb.row <= row && 1 <= nb.col && nb.col <= col) yield nb
        neighborMap += ((r, c) -> trueNeighbors)
      }
    }
    neighborTables += ((row, col) -> neighborMap)
  }

  def initCornerTable(row: Int, col: Int): Unit = {
    val cornerMap: mutable.Map[(Int, Int), List[Point]] = mutable.Map.empty
    for (r <- 1 to row) {
      for (c <- 1 to col) {
        val point = Point(r, c)
        val allCorners = List(
          Point(point.row - 1, point.col - 1),
          Point(point.row + 1, point.col + 1),
          Point(point.row - 1, point.col + 1),
          Point(point.row + 1, point.col - 1)
        )
        val trueCorners =
          for (nb <- allCorners if 1 <= nb.row && nb.row <= row && 1 <= nb.col && nb.col <= col) yield nb
        cornerMap += ((r, c) -> trueCorners)
      }
    }
    cornerTables += ((row, col) -> cornerMap)
  }

}
