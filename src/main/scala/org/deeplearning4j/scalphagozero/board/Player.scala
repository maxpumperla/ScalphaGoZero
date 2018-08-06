package org.deeplearning4j.scalphagozero.board

/**
  * Player of a game of Go (either black or white)
  *
  * @param color
  */
class Player(val color: Int) {

  if (!color.equals(PlayerColor.black) && !color.equals(PlayerColor.white))
    throw new IllegalArgumentException(s"Only black and white allowed as player. got $color")

  def other: Player = if (this.color.equals(PlayerColor.black)) Player(PlayerColor.white) else Player(PlayerColor.black)
}

object Player {
  def apply(color: Int) = new Player(color)
}
