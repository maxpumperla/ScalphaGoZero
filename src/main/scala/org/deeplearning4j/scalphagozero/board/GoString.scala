package org.deeplearning4j.scalphagozero.board

/**
  * A Go string is a collection of stones of given color and corresponding liberties
  *
  * @param color stone color
  * @param stones stones of the Go string
  * @param liberties liberties of the Go string
  *
  * @author Max Pumperla
  */
final case class GoString(color: PlayerColor, stones: Set[(Int, Int)], liberties: Set[(Int, Int)]) {

  def numLiberties: Int = liberties.size

  def withoutLiberty(point: Point): GoString = {
    val newLiberties = this.liberties - point.toCoords
    GoString(this.color, this.stones, newLiberties)
  }

  def withLiberty(point: Point): GoString = {
    val newLiberties = this.liberties + point.toCoords
    GoString(this.color, this.stones, newLiberties)
  }

  def mergedWith(goString: GoString): GoString = {
    if (!color.equals(goString.color))
      throw new IllegalArgumentException("Color of Go strings has to match")

    val combinedStones = stones ++ goString.stones
    val commonLiberties = (liberties ++ goString.liberties) -- combinedStones
    GoString(color, combinedStones, commonLiberties)
  }

}
