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
class GoString(val color: Int, var stones: Set[Point], var liberties: Set[Point]) {

  def numLiberties: Int = liberties.size

  def withoutLiberty(point: Point): GoString = {
    val newLiberties = this.liberties - point
    GoString(this.color, this.stones, newLiberties)
  }

  def withLiberty(point: Point): GoString = {
    val newLiberties = this.liberties + point
    GoString(this.color, this.stones, newLiberties)
  }

  def mergedWith(goString: GoString): GoString = {
    if (!color.equals(goString.color))
      throw new IllegalArgumentException("Color of Go strings has to match")
    val combinedStones = stones ++ goString.stones
    val commonLiberties = (liberties ++ goString.liberties) -- combinedStones
    GoString(color, combinedStones, commonLiberties)
  }

  override def equals(obj: scala.Any): Boolean = {
    if (obj.isInstanceOf[GoString]) {
      val castObj = obj.asInstanceOf[GoString]
      return this.color.equals(castObj.color) && this.stones.equals(castObj.stones) && this.liberties
        .equals(castObj.liberties)

    }
    return false
  }
}

object GoString {

  def apply(color: Int, stones: Set[Point], liberties: Set[Point]): GoString =
    new GoString(color, stones, liberties)
}
