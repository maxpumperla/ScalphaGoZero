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

  def neighbors: List[Point] = List(
    Point(this.row - 1, this.col),
    Point(this.row + 1, this.col),
    Point(this.row, this.col - 1),
    Point(this.row, this.col + 1)
  )

  def toCoords: (Int, Int) = (this.row, this.col)

}
