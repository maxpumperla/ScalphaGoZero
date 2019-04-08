package org.deeplearning4j.scalphagozero.board

object NeighborTables {

  private var neighborTables: Map[Int, Map[Point, List[Point]]] = Map()

  def getNbrTable(size: Int): Map[Point, List[Point]] = {
    if (!neighborTables.contains(size)) {
      initNeighborTable(size)
    }
    neighborTables(size)
  }

  private def initNeighborTable(size: Int): Unit = {
    var neighborMap: Map[Point, List[Point]] = Map()
    for (r <- 1 to size; c <- 1 to size) {
      val point = Point(r, c)
      val allNeighbors = point.neighbors
      val trueNeighbors = inRange(size, allNeighbors)
      neighborMap += (point -> trueNeighbors)
    }
    neighborTables += (size -> neighborMap)
  }

  private def inRange(size: Int, points: List[Point]): List[Point] =
    for (nb <- points if 1 <= nb.row && nb.row <= size && 1 <= nb.col && nb.col <= size) yield nb
}
