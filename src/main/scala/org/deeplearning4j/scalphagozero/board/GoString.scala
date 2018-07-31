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

  def removeLiberty(point: Point): Unit = liberties -= point

  def addLiberty(point: Point): Unit = liberties += point

  def mergedWith(goString: GoString): GoString = {
    if (!color.equals(goString.color))
      throw new IllegalArgumentException("Color of Go strings has to match")
    val combinedStones = stones ++ goString.stones
    val commonLiberties = (liberties ++ goString.liberties) -- combinedStones
    GoString(color, combinedStones, commonLiberties)
  }

  def equals(other: GoString): Boolean =
    this.color.equals(other.color) && this.stones.equals(other.stones) && this.liberties.equals(other.liberties)
}

object GoString {

  def apply(color: Int, stones: Set[Point], liberties: Set[Point]): GoString =
    new GoString(color, stones, liberties)
}
