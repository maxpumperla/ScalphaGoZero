package org.deeplearning4j.scalphagozero.agents

/**
  * Branch of an AlphaGo Zero tree.
  *
  * @param prior prior move probability
  * @param visitCount visit count of the node
  * @param totalValue accumulated total value of the node
  */
final case class Branch(prior: Double, visitCount: Int = 0, totalValue: Double = 0)
