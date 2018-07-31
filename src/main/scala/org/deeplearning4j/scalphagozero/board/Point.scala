package org.deeplearning4j.scalphagozero.board

/**
  * Go board point class.
  *
  * @param row row index
  * @param col column index
  *
  * @author Max Pumperla
  */
class Point(val row: Int, val col: Int) {

  def neighbors: List[Point] = List(
    Point(this.row - 1, this.col),
    Point(this.row + 1, this.col),
    Point(this.row, this.col - 1),
    Point(this.row, this.col + 1)
  )

}

/**
  * Go board point object
  *
  * @author Max Pumperla
  */
object Point {

  def apply(row: Int, col: Int): Point = new Point(row, col)

}
