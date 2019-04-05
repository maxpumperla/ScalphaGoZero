package org.deeplearning4j.scalphagozero.board

/**
  * Go board point class.
  *
  * @param row row index
  * @param col column index
  *
  * @author Max Pumperla
  */
final case class Point(row: Int, col: Int) {

  /** @return strongly connected neighbors */
  def neighbors: List[Point] = List(
    Point(row - 1, col),
    Point(row + 1, col),
    Point(row, col - 1),
    Point(row, col + 1)
  )

  /** @return adjacent diagonals from this point */
  def diagonals: List[Point] = List(
    Point(row - 1, col - 1),
    Point(row + 1, col + 1),
    Point(row - 1, col + 1),
    Point(row + 1, col - 1)
  )

  def toCoords: (Int, Int) = (this.row, this.col)

}
