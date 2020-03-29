package org.deeplearning4j.scalphagozero.board

/**
  * Main Go board class. Represents the board on which Go moves can be played. Immutable.
  * Internally, a grid keeps track of the strings at each vertex.
  *
  * @param size the size of the go board. Values of 5, 9, 13, 17, 19, or 25 are reasonable.
  * @param grid manages association of stones with parent strings.
  * @author Max Pumperla
  * @author Barry Becker
  */
case class GoBoard(size: Int, grid: Grid = Grid(), blackCaptures: Int = 0, whiteCaptures: Int = 0) {

  private val serializer = new GoBoardSerializer()
  private val neighborMap = NeighborTables.getNbrTable(size)
  private val diagonalMap = NeighborTables.getDiagnonalTable(size)

  def placeStone(player: Player, point: Point): GoBoard = {
    assert(isOnGrid(point), point + " was not on the grid!")

    if (grid.getString(point).isDefined) {
      println(" Illegal move attempted at: " + point + ". Already occupied: " + grid.getString(point).get)
      this
    } else makeValidStonePlacement(player, point)
  }

  private def makeValidStonePlacement(player: Player, point: Point): GoBoard = {
    var (newGrid, stringsToRemove) = determineStringsToRemove(player, point)

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

  private def determineStringsToRemove(player: Player, point: Point): (Grid, Set[GoString]) = {
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
    (newGrid, stringsToRemove)
  }

  def isCorner(point: Point): Boolean =
    (point.row == 1 && point.col == 1) ||
    (point.row == size && point.col == 1) ||
    (point.row == 1 && point.col == size) ||
    (point.row == size && point.col == size)

  def isEdge(point: Point): Boolean =
    point.row == 1 || point.col == 1 || point.row == size || point.col == size

  /**
    * A player should never fill her own eye, but determining a true eye is not that easy.
    * @return true if the specified play fills that player's eye
    */
  def doesMoveFillEye(player: Player, point: Point): Boolean = {
    val nbrs = findNumNeighbors(player, point, neighborMap)
    val diagNbrs = findNumNeighbors(player, point, diagonalMap)
    val allNbrs = nbrs + diagNbrs

    point match {
      case cornerPt if isCorner(cornerPt) => allNbrs == 3
      case edgePt if isEdge(edgePt)       => allNbrs == 5
      case _                              => nbrs == 4 && diagNbrs >= 3
    }
  }

  private def findNumNeighbors(player: Player, point: Point, nbrMap: Map[Point, List[Point]]): Int = {
    nbrMap(point).count(neighbor => {
      val str = grid.getString(neighbor)
      (str.isDefined && str.get.player == player) || (str.isEmpty && isAncillaryEye(player, neighbor))
    })
  }

  private def isAncillaryEye(player: Player, point: Point): Boolean = {
    val nbrs = findNumTrueNeighbors(player, point, neighborMap)
    val diagNbrs = findNumTrueNeighbors(player, point, diagonalMap)
    val allNbrs = nbrs + diagNbrs

    point match {
      case cornerPt if isCorner(cornerPt) => allNbrs == 2
      case edgePt if isEdge(edgePt)       => allNbrs == 4
      case _                              => nbrs == 4 && diagNbrs >= 2
    }
  }

  private def findNumTrueNeighbors(player: Player, point: Point, nbrMap: Map[Point, List[Point]]): Int = {
    nbrMap(point).count(neighbor => {
      val str = grid.getString(neighbor)
      str.isDefined && str.get.player == player
    })
  }

  /** @return true if player playing at point will capture stones */
  def willCapture(player: Player, point: Point): Boolean =
    neighborMap(point).exists { pt => {
        val nbrStr = grid.getString(pt)
        nbrStr.isDefined && nbrStr.get.player != player && nbrStr.get.numLiberties == 1
      }
    }

  def isOnGrid(point: Point): Boolean = 1 <= point.row && point.row <= size && 1 <= point.col && point.col <= size
  def getPlayer(point: Point): Option[Player] = grid.getPlayer(point)
  def getGoString(point: Point): Option[GoString] = grid.getString(point)
  def zobristHash: Long = grid.hash

  override def toString: String = serializer.serialize(this)
}
