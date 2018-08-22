package org.deeplearning4j.scalphagozero.agents

/**
  * Branch of an AlphaGo Zero tree.
  *
  * @param prior prior move probability
  * @param visitCount visit count of the node
  * @param totalValue accumulated total value of the node
  */
class Branch(val prior: Double, val visitCount: Int = 0, val totalValue: Double = 0)
