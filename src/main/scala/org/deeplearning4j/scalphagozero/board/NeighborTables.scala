package org.deeplearning4j.scalphagozero.board

object NeighborTables {

  private var neighborTables: Map[Int, NeighborMap] = Map()
  private var diagonalTables: Map[Int, NeighborMap] = Map()

  /**
    * @param size size of the board to create map for
    * @return a map from a point on the board to its (up to) 4 neighbors, for specified board size.
    */
  def getNbrTable(size: Int): NeighborMap = {
    if (!neighborTables.contains(size)) {
      neighborTables += size -> createNeighborMap(size)
    }
    neighborTables(size)
  }

  def getDiagnonalTable(size: Int): NeighborMap = {
    if (!diagonalTables.contains(size)) {
      diagonalTables += size -> createDiagonalNeighborMap(size)
    }
    diagonalTables(size)
  }

  private def createNeighborMap(size: Int): NeighborMap = {
    var neighborMap = new NeighborMap()
    for (r <- 1 to size; c <- 1 to size) {
      val point = Point(r, c)
      val allNeighbors = point.neighbors
      val trueNeighbors = inRange(size, allNeighbors)
      neighborMap += (Point(r, c) -> trueNeighbors)
    }
    neighborMap
  }

  /** For each point in the grid, the map has the diagonals from that point */
  private def createDiagonalNeighborMap(size: Int): NeighborMap = {
    var diagonalMap = new NeighborMap()
    for (r <- 1 to size; c <- 1 to size) {
      val point = Point(r, c)
      val allDiagonals = point.diagonals
      val trueDiagonals = inRange(size, allDiagonals)
      diagonalMap += (Point(r, c) -> trueDiagonals)
    }
    diagonalMap
  }

  private def inRange(size: Int, points: List[Point]): List[Point] =
    for (nbr <- points if 1 <= nbr.row && nbr.row <= size && 1 <= nbr.col && nbr.col <= size) yield nbr
}
