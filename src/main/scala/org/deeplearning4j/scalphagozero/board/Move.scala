package org.deeplearning4j.scalphagozero.board

/**
  * Go move class. A move can either be playing a point on the board, passing or resigning
  *
  * @param point optional point to be played
  * @param isPass is move a pass?
  * @param isResign is move resignation?
  */
class Move(val point: Option[Point] = None, val isPass: Boolean = false, val isResign: Boolean = false) {

  def isPlay: Boolean = point.isDefined

  override def toString(): String = {
    if (isPass) "pass"
    if (isResign) "resign"
    "row: " + point.get.row + " col: " + point.get.col
  }

}

object Move {

  def play(point: Point): Move = new Move(Some(point))

  def pass(): Move = new Move(None, true, false)

  def resign(): Move = new Move(None, false, true)

}
