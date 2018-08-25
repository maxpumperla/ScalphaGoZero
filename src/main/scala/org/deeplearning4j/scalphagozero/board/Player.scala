package org.deeplearning4j.scalphagozero.board
import org.deeplearning4j.scalphagozero.board.PlayerColor.{ Black, White }

/**
  * Player of a game of Go (either black or white)
  *
  * @param color player color, 1 = black, 2 = white
  */
final case class Player(color: PlayerColor) {

  def other: Player =
    color match {
      case Black => Player(White)
      case White => Player(Black)
    }

}
