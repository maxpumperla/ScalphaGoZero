package org.deeplearning4j.scalphagozero.board

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import ZobristHashing.ZOBRIST

/**
  * Main Go board class, represents the board on which Go moves can be played.
  *
  * @author Max Pumperla
  */
class GoBoard(val size: Int) {

  /** map from board grid points to parent string (if any) */
  private var grid: mutable.Map[(Int, Int), GoString] = mutable.Map.empty
  private var hash: Long = 0L
  private val serializer = new GoBoardSerializer(this)

  private var neighborMap: Map[(Int, Int), List[Point]] = NeighborTables.getNbrTable(size)
  private var diagonalMap: Map[(Int, Int), List[Point]] = NeighborTables.getDiagnonalTable(size)

  def neighbors(point: Point): List[Point] = neighborMap.getOrElse((point.row, point.col), List.empty)

  def corners(point: Point): List[Point] = diagonalMap.getOrElse((point.row, point.col), List.empty)

  def placeStone(player: Player, point: Point): Unit = {
    assert(isOnGrid(point))

    if (getGoString(point).isDefined) {
      println(" Illegal move attempted at: " + point.toCoords)
    } else {
      // 1. Examine adjacent points
      val adjacentSameColor = mutable.Set.empty[GoString]
      val adjacentOppositeColor = mutable.Set.empty[GoString]
      val liberties = mutable.Set.empty[(Int, Int)]

      for (neighbor: Point <- neighborMap((point.row, point.col))) {
        getGoString(neighbor) match {
          case None                                        => liberties += neighbor.toCoords
          case Some(goString) if goString.player == player => adjacentSameColor += goString
          case Some(goString)                              => adjacentOppositeColor += goString
        }
      }

      val newString =
        (adjacentSameColor += GoString(player, Set(point.toCoords), liberties.toSet)).reduce(_ mergedWith _)

      for (newStringPoint: (Int, Int) <- newString.stones)
        grid.put(newStringPoint, newString)

      hash ^= ZOBRIST((point, None)) // Remove empty-point hash code
      hash ^= ZOBRIST((point, Some(player))) // Add filled point hash code.

      // 3. Reduce liberties of any adjacent strings of the opposite color.
      // 4. If any opposite color strings now have zero liberties, remove them.
      for (otherColorString: GoString <- adjacentOppositeColor) {
        val replacement = otherColorString.withoutLiberty(point)
        if (replacement.numLiberties > 0) replaceString(replacement) else removeString(otherColorString)
      }
    }
  }

  private def removeString(goString: GoString): Unit =
    goString.stones.foreach { point =>
      neighborMap((point._1, point._2)).foreach { neighbor =>
        getGoString(neighbor) match {
          case Some(neighborString) if neighborString == goString =>
            this.replaceString(neighborString.withLiberty(Point(point._1, point._2)))
          case _ => ()
        }

        grid.remove(point)
      }

      hash ^= ZOBRIST((new Point(point), Some(goString.player))) //Remove filled point hash code.
      hash ^= ZOBRIST((new Point(point), None)) //Add empty point hash code.
    }

  private def replaceString(newString: GoString): Unit =
    for (point <- newString.stones)
      grid += (point -> newString)

  def isSelfCapture(player: Player, point: Point): Boolean = {
    val friendlyStrings: ListBuffer[GoString] = ListBuffer.empty[GoString]

    for (neighbor <- neighborMap((point.row, point.col))) {
      getGoString(neighbor) match {
        case None                                                     => return false
        case Some(neighborString) if neighborString.player == player  => friendlyStrings += neighborString
        case Some(neighborString) if neighborString.numLiberties == 1 => return false
        case _                                                        => ()
      }
    }

    friendlyStrings.forall(_.numLiberties == 1)
  }

  def willCapture(player: Player, point: Point): Boolean =
    neighborMap((point.row, point.col)).exists {
      getGoString(_) match {
        case Some(neighborString) if neighborString.player != player && neighborString.numLiberties == 1 => true
        case _                                                                                           => false
      }
    }

  def isOnGrid(point: Point): Boolean = 1 <= point.row && point.row <= size && 1 <= point.col && point.col <= size

  def getPlayer(point: Point): Option[Player] = grid.get(point.toCoords).map(_.player)

  def getGoString(point: Point): Option[GoString] = grid.get(point.toCoords)

  override def equals(obj: scala.Any): Boolean = {
    obj match {
      case other: GoBoard =>
        return this.size == other.size && this.grid.equals(other.grid)
      case _ =>
    }
    false
  }

  def zobristHash: Long = hash

  private def setBoardProperties(
      grid: mutable.Map[(Int, Int), GoString],
      hash: Long,
      neighborMap: Map[(Int, Int), List[Point]],
      cornerMap: Map[(Int, Int), List[Point]]
  ): Unit = {
    this.hash = hash
    this.grid = grid
    this.neighborMap = neighborMap
    this.diagonalMap = cornerMap
  }

  override def clone(): GoBoard = {
    val newBoard = new GoBoard(size)
    newBoard.setBoardProperties(grid, hash, neighborMap, diagonalMap)
    newBoard
  }

  override def toString: String = serializer.serialize()
}
