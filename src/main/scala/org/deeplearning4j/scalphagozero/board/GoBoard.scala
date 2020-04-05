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
  private val boundsChecker = GoBoardBoundsChecker.get(size)
  private val neighborMap = NeighborTables.getNbrTable(size)
  private val diagonalMap = NeighborTables.getDiagnonalTable(size)

  def placeStone(player: Player, point: Point): GoBoard = {
    assert(boundsChecker.inBounds(point), point + " was not on the grid!")

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

  /**
    * A player should never fill her own eye, but determining a true eye is not that easy.
    * @return true if the specified play fills that player's eye
    */
  def doesMoveFillEye(player: Player, point: Point): Boolean = {
    val neighbors = findNumNeighbors(player, point, neighborMap)
    val diagNeighbors = findNumNeighbors(player, point, diagonalMap)
    val allNbrs = neighbors + diagNeighbors

    point match {
      case cornerPt if boundsChecker.isCorner(cornerPt) => allNbrs == 3
      case edgePt if boundsChecker.isEdge(edgePt) => allNbrs == 5
      case _ => neighbors == 4 && diagNeighbors >= 3
    }
  }

  /**
    * @return the number of neighbors that are either the same color stone or an eye for that group
    */
  private def findNumNeighbors(player: Player, point: Point, nbrMap: NeighborMap): Int =
    nbrMap(point).count(neighbor => {
      val str = grid.getString(neighbor)
      (str.isDefined && str.get.player == player) || (str.isEmpty && isAncillaryEye(player, neighbor))
    })

  private def isAncillaryEye(player: Player, point: Point): Boolean = {
    val neighbors = neighborMap.findNumTrueNeighbors(player, point, grid)
    val diagNeighbors = diagonalMap.findNumTrueNeighbors(player, point, grid)
    val allNbrs = neighbors + diagNeighbors

    point match {
      case cornerPt if boundsChecker.isCorner(cornerPt) => allNbrs == 2
      case edgePt if boundsChecker.isEdge(edgePt) => allNbrs == 4
      case _ => neighbors == 4 && diagNeighbors >= 2
    }
  }

  /** @return true if player playing at point will capture stones */
  def willCapture(player: Player, point: Point): Boolean =
    neighborMap(point).exists { pt =>
      {
        val nbrStr = grid.getString(pt)
        nbrStr.isDefined && nbrStr.get.player != player && nbrStr.get.numLiberties == 1
      }
    }

  def inBounds(point: Point): Boolean = boundsChecker.inBounds(point)
  def getPlayer(point: Point): Option[Player] = grid.getPlayer(point)
  def getGoString(point: Point): Option[GoString] = grid.getString(point)
  def zobristHash: Long = grid.hash

  override def toString: String = serializer.serialize(this)
}
