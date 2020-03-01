package org.deeplearning4j.scalphagozero.scoring

sealed trait VertexType extends Serializable {
  def isTerritory = true
}

case object BlackStone extends VertexType { override def isTerritory = false}
case object WhiteStone extends VertexType { override def isTerritory = false}
case object CapturedBlackStone extends VertexType
case object CapturedWhiteStone extends VertexType
case object BlackTerritory extends VertexType
case object WhiteTerritory extends VertexType
case object Dame extends VertexType
