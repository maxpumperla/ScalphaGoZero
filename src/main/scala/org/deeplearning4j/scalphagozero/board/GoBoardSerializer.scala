package org.deeplearning4j.scalphagozero.board

import GoBoardSerializer.COORDS

/**
  *
  * @param board the go board to serialize
  * @author Barry Becker
  */
class GoBoardSerializer(board: GoBoard) {

  def serialize(): String = {

    var s = "-" * (board.size + 2) + "\n"

    s += "  " + COORDS.substring(0, board.size) + "\n"
    for (i <- 1 to board.size) {
      s += COORDS(i - 1) + " "
      for (j <- 1 to board.size) {
        val player = board.getPlayer(Point(i, j))
        val symb = if (player.isEmpty) "." else if (player.get == BlackPlayer) "X" else "O"
        s += symb
      }
      s += "\n"
    }
    s + "-" * (board.size + 2)
  }
}

object GoBoardSerializer {
  private val COORDS: String = "123456789abcdefghijklmnopqrstuvwxyz"
}
