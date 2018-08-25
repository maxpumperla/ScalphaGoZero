package org.deeplearning4j.scalphagozero.board

/**
  * Player of a game of Go (either black or white)
  */
sealed trait Player {
  def other: Player = this match {
    case BlackPlayer => WhitePlayer
    case WhitePlayer => BlackPlayer
  }
}
case object BlackPlayer extends Player
case object WhitePlayer extends Player
