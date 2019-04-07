package org.deeplearning4j.scalphagozero.board

import ZobristHashing.ZOBRIST

/**
  * Main Go board class, represents the board on which Go moves can be played. Immutable.
  *
  * @param size the size of the go board. Values of 5, 9, 13, 17, 19, or 25 are reasonable.
  * @param grid map from grid point location to parent string of stones (if any)
  * @param hash the Zobrist hash. Gets updated as moves are played.
  * @author Max Pumperla
  * @author Barry Becker
  */
case class GoBoard(size: Int, grid: Map[(Int, Int), GoString] = Map(), hash: Long = 0L) {

  private val serializer = new GoBoardSerializer(this)

  private val neighborMap: Map[(Int, Int), List[Point]] = NeighborTables.getNbrTable(size)
  private val diagonalMap: Map[(Int, Int), List[Point]] = NeighborTables.getDiagnonalTable(size)

  def neighbors(point: Point): List[Point] = neighborMap.getOrElse((point.row, point.col), List.empty)

  def corners(point: Point): List[Point] = diagonalMap.getOrElse((point.row, point.col), List.empty)

  def placeStone(player: Player, point: Point): GoBoard = {
    assert(isOnGrid(point))

    if (getGoString(point).isDefined) {
      println(" Illegal move attempted at: " + point.toCoords + ". Already occupied: " + getGoString(point).get)
      this
    } else {
      // 1. Examine adjacent points
      var adjacentSameColor = Set[GoString]()
      var adjacentOppositeColor = Set[GoString]()
      var liberties = Set[(Int, Int)]()

      for (neighbor: Point <- neighborMap((point.row, point.col))) {
        getGoString(neighbor) match {
          case None                                        => liberties += neighbor.toCoords
          case Some(goString) if goString.player == player => adjacentSameColor += goString
          case Some(goString)                              => adjacentOppositeColor += goString
        }
      }

      // 2. Merge any strings of the same color adjacent to the placed stone
      adjacentSameColor += GoString(player, Set(point.toCoords), liberties)
      val newString: GoString = adjacentSameColor.reduce(_ mergedWith _)

      var newGrid = grid
      for (newStringPoint: (Int, Int) <- newString.stones)
        newGrid += newStringPoint -> newString

      var newHash = hash
      newHash ^= ZOBRIST((point, None)) // Remove empty-point hash code
      newHash ^= ZOBRIST((point, Some(player))) // Add filled point hash code.

      // 3. Reduce liberties of any adjacent strings of the opposite color.
      // 4. If any opposite color strings now have zero liberties, remove them.
      var stringsToRemove = Set[GoString]()
      for (otherColorString: GoString <- adjacentOppositeColor) {
        val otherString = otherColorString.withoutLiberty(point)
        if (otherString.numLiberties > 0) {
          newGrid = replaceString(otherString, newGrid)
        } else stringsToRemove += otherString
      }

      stringsToRemove.foreach(str => {
        val (nGrid, nHash) = removeString(str, newGrid, newHash)
        newGrid = nGrid
        newHash = nHash
      })

      GoBoard(size, newGrid, newHash)
    }
  }

  /**
    * When a string is removed due to capture, also update the liberties of the adjacent strings of opposite color.
    * @param goString the string to remove
    * @return newGrid and newHash value
    */
  private def removeString(
      goString: GoString,
      grid: Map[(Int, Int), GoString],
      hash: Long
  ): (Map[(Int, Int), GoString], Long) = {
    var newGrid = grid
    var newHash = hash

    // first remove the stones from the board
    goString.stones.foreach { point =>
      newGrid -= point // the point is now empty
      newHash ^= ZOBRIST((new Point(point), Some(goString.player))) //Remove filled point hash code.
      newHash ^= ZOBRIST((new Point(point), None)) //Add empty point hash code.
    }

    // for each opponent neighbor string add a liberty for each adjacent removed point
    goString.stones.foreach { point =>
      neighborMap(point).foreach { neighbor =>
        val oppNbrString = getGoString(neighbor, newGrid)
        if (oppNbrString.nonEmpty) {
          newGrid = replaceString(oppNbrString.get.withLiberty(Point(point._1, point._2)), newGrid)
        }
      }
    }
    (newGrid, newHash)
  }

  private def replaceString(newString: GoString, grid: Map[(Int, Int), GoString]): Map[(Int, Int), GoString] = {
    var newGrid = grid
    for (point <- newString.stones)
      newGrid += (point -> newString)
    newGrid
  }

  def isSelfCapture(player: Player, point: Point): Boolean = {
    var friendlyStrings: List[GoString] = List[GoString]()

    for (neighbor <- neighborMap((point.row, point.col))) {
      getGoString(neighbor) match {
        case None                                                => return false
        case Some(friendNbrStr) if friendNbrStr.player == player => friendlyStrings :+= friendNbrStr
        case Some(oppNbrStr) if oppNbrStr.numLiberties == 1      => return false
        case _                                                   => new IllegalStateException("nbr=" + neighbor)
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

  def getGoString(point: Point, myGrid: Map[(Int, Int), GoString] = grid): Option[GoString] = myGrid.get(point.toCoords)

  override def equals(obj: scala.Any): Boolean = {
    obj match {
      case other: GoBoard =>
        return this.size == other.size && this.grid.equals(other.grid)
      case _ =>
    }
    false
  }

  override def toString: String = serializer.serialize()
}
