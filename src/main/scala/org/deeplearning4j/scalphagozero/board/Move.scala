package org.deeplearning4j.scalphagozero.board

/**
  * Go move class. A move can either be playing a point on the board, passing or resigning
  */
sealed trait Move extends Product with Serializable

object Move {
  case object Pass extends Move
  case object Resign extends Move
  final case class Play(point: Point) extends Move {
    override def toString: String = s"Play(${point.row},${point.col})"
  }
  object Play {
    def apply(row: Int, col: Int): Play = Play(Point(row, col))
  }
}
