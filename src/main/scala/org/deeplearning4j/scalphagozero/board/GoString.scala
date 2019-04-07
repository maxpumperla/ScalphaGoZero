package org.deeplearning4j.scalphagozero.board

/**
  * A Go string is a collection of stones of given color and corresponding liberties
  *
  * @param player stone color
  * @param stones stones of the Go string
  * @param liberties liberties of the Go string
  *
  * @author Max Pumperla
  */
final case class GoString(player: Player, stones: Set[(Int, Int)], liberties: Set[(Int, Int)]) {

  val numLiberties: Int = liberties.size

  def withoutLiberty(point: Point): GoString = {
    val newLiberties = this.liberties - point.toCoords
    GoString(player, stones, newLiberties)
  }

  def withLiberty(point: Point): GoString = {
    val newLiberties = this.liberties + point.toCoords
    GoString(player, stones, newLiberties)
  }

  def mergedWith(goString: GoString): GoString = {
    if (!player.equals(goString.player))
      throw new IllegalArgumentException("Color of Go strings has to match")

    val combinedStones = stones ++ goString.stones
    val commonLiberties = (liberties ++ goString.liberties) -- combinedStones
    GoString(player, combinedStones, commonLiberties)
  }

}
