package org.deeplearning4j.scalphagozero.board

object GoBoardBoundsChecker {

  private var map: Map[Int, GoBoardBoundsChecker] = Map()

  def get(size: Int): GoBoardBoundsChecker = {
    if (!map.contains(size)) size
      map += size -> new GoBoardBoundsChecker(size)
    map(size)
  }
}

case class GoBoardBoundsChecker(size: Int) {

  def inBounds(point: Point): Boolean =
    1 <= point.row && point.row <= size && 1 <= point.col && point.col <= size

  def isCorner(point: Point): Boolean =
    (point.row == 1 && point.col == 1) ||
      (point.row == size && point.col == 1) ||
      (point.row == 1 && point.col == size) ||
      (point.row == size && point.col == size)

  def isEdge(point: Point): Boolean =
    point.row == 1 || point.col == 1 || point.row == size || point.col == size
}
