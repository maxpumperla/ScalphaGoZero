package org.deeplearning4j.scalphagozero.board

sealed trait PlayerColor extends Product with Serializable
object PlayerColor {
  case object Black extends PlayerColor
  case object White extends PlayerColor
}
