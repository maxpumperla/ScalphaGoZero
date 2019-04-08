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

  def this(tuple: (Int, Int)) = this(tuple._1, tuple._2)

  /** @return strongly connected neighbors */
  def neighbors: List[Point] = List(
    Point(row - 1, col),
    Point(row + 1, col),
    Point(row, col - 1),
    Point(row, col + 1)
  )
}
