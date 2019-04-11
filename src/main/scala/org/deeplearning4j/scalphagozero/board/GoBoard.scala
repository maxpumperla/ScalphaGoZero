package org.deeplearning4j.scalphagozero.board

/**
  * Main Go board class, represents the board on which Go moves can be played. Immutable.
  * Internally, a grid keeps track of the strings at each vertex.
  *
  * @param size the size of the go board. Values of 5, 9, 13, 17, 19, or 25 are reasonable.
  * @param grid manages association of stones with parent strings.
  * @author Max Pumperla
  * @author Barry Becker
  */
case class GoBoard(size: Int, grid: Grid = Grid(), blackCaptures: Int = 0, whiteCaptures: Int = 0) {

  private val serializer = new GoBoardSerializer(this)
  private val neighborMap: Map[Point, List[Point]] = NeighborTables.getNbrTable(size)

  def placeStone(player: Player, point: Point): GoBoard = {
    assert(isOnGrid(point))

    if (grid.getString(point).isDefined) {
      println(" Illegal move attempted at: " + point + ". Already occupied: " + grid.getString(point).get)
      this
    } else {
      // 1. Examine adjacent points
      var adjacentSameColor = Set[GoString]()
      var adjacentOppositeColor = Set[GoString]()
      var liberties = Set[Point]()

      for (neighbor: Point <- neighborMap(point)) {
        grid.getString(neighbor) match {
          case None                                        => liberties += neighbor
          case Some(goString) if goString.player == player => adjacentSameColor += goString
          case Some(goString)                              => adjacentOppositeColor += goString
        }
      }

      // 2. Merge any strings of the same color adjacent to the placed stone
      adjacentSameColor += GoString(player, Set(point), liberties)
      val newString: GoString = adjacentSameColor.reduce(_ mergedWith _)

      var newGrid = grid.updateStringWhenAddingStone(point, newString)

      // 3. Reduce liberties of any adjacent strings of the opposite color.
      // 4. If any opposite color strings now have zero liberties, remove them.
      var stringsToRemove = Set[GoString]()
      for (otherColorString: GoString <- adjacentOppositeColor) {
        val otherString = otherColorString.withoutLiberty(point)
        if (otherString.numLiberties > 0) {
          newGrid = newGrid.replaceString(otherString)
        } else stringsToRemove += otherString
      }

      var newBlackCaptures = blackCaptures
      var newWhiteCaptures = whiteCaptures
      stringsToRemove.foreach(str => {
        player match {
          case BlackPlayer => newBlackCaptures += str.size
          case WhitePlayer => newWhiteCaptures += str.size
        }
        newGrid = newGrid.removeString(str, neighborMap)
      })

      GoBoard(size, newGrid, newBlackCaptures, newWhiteCaptures)
    }
  }

  def isSelfCapture(player: Player, point: Point): Boolean = {
    var friendlyStrings: List[GoString] = List[GoString]()

    for (neighbor <- neighborMap(point)) {
      grid.getString(neighbor) match {
        case None                                                => return false
        case Some(friendNbrStr) if friendNbrStr.player == player => friendlyStrings :+= friendNbrStr
        case Some(oppNbrStr) if oppNbrStr.numLiberties == 1      => return false
        case _                                                   => new IllegalStateException("nbr=" + neighbor)
      }
    }

    friendlyStrings.forall(_.numLiberties == 1)
  }

  /** @return true if player playing at point will capture stones */
  def willCapture(player: Player, point: Point): Boolean =
    neighborMap(point).exists { pt =>
      {
        val nbrStr = grid.getString(pt)
        nbrStr.isDefined && nbrStr.get.player != player && nbrStr.get.numLiberties == 1
      }
    }

  def isOnGrid(point: Point): Boolean = 1 <= point.row && point.row <= size && 1 <= point.col && point.col <= size
  def getPlayer(point: Point): Option[Player] = grid.getPlayer(point)
  def getGoString(point: Point): Option[GoString] = grid.getString(point)
  def zobristHash: Long = grid.hash

  override def toString: String = serializer.serialize()
}
