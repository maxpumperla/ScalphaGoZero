package org.deeplearning4j.scalphagozero.agents

import scala.util.Random

/**
  * @param a the probability distribution. Each entry is in [0, 1]
  */
case class ProbabilityDistribution(a: Array[Double], rnd: Random) {
  assert(a.nonEmpty)
  private val sum = a.sum

  /**
    * @return an index selected stochastically from a probability distribution defined by a.
    *         If all the values of a are 0, then the first is returned.
    */
  def selectRandomIdx(): Int = {
    if (sum == 0) 0
    else {
      val r = rnd.nextDouble() * sum
      var t: Double = 0
      var ct = 0
      val len = a.length
      while (t <= r && ct < len) {
        t += a(ct)
        ct += 1
      }
      if (ct == 0) println("bad a distribution = " + a.mkString(", "))
      ct - 1
    }
  }
}
