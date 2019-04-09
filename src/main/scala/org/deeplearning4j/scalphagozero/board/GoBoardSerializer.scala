package org.deeplearning4j.scalphagozero.board

import GoBoardSerializer.X_COORD

/**
  *
  * @param board the go board to serialize
  * @author Barry Becker
  */
class GoBoardSerializer(board: GoBoard) {

  def serialize(): String = {

    var s = "-" * (board.size + 3) + "\n"

    for (i <- 1 to board.size) {
      val rowNum = board.size + 1 - i
      s += (if (rowNum < 10) " " else "") + rowNum + " "
      for (j <- 1 to board.size) {
        val player = board.getPlayer(Point(i, j))
        val symb = if (player.isEmpty) "." else if (player.get == BlackPlayer) "X" else "O"
        s += symb
      }
      s += "\n"
    }
    s += "   " + X_COORD.substring(0, board.size) + "\n"
    s + "-" * (board.size + 3)
  }
}

object GoBoardSerializer {
  private val X_COORD: String = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
}
