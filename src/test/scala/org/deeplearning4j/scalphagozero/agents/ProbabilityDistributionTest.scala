package org.deeplearning4j.scalphagozero.agents

import java.util.Random
import org.scalatest.funspec.AnyFunSpec

class ProbabilityDistributionTest extends AnyFunSpec {

  describe("Select from a distribution") {

    it("should be low index if distribution skewed low") {
      val dist = createPDist(Array(0.9, 0.8, 0.5, 0.3, 0.2, 0.1, 0.01, 0.001))
      assert(dist.selectRandomIdx() == 2)
      assert(dist.selectRandomIdx() == 1)
      assert(dist.selectRandomIdx() == 0)
    }

    it("should be high index if distribution skewed high") {
      val dist = createPDist(Array(0.001, 0.01, 0.1, 0.3, 0.8, 0.5, 0.8, 0.9))
      assert(dist.selectRandomIdx() == 6)
    }

    it("should be highest index if distribution skewed very high") {
      val dist = createPDist(Array(0.001, 0.01, 0.01, 0.01, 0.01, 0.1, 0.9))
      assert(dist.selectRandomIdx() == 6)
    }

    it("should be near middle if gaussian distribution") {
      val dist = createPDist(Array(0.001, 0.01, 0.1, 0.3, 0.6, 0.8, 0.9, 0.9, 0.8, 0.55, 0.4, 0.2, 0.05, 0.01))
      assert(dist.selectRandomIdx() == 8)
    }

    it("random if uniform distribution") {
      val dist = createPDist(Array(0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2))
      assert(dist.selectRandomIdx() == 5)
    }

    it("should be 0 index if distribution has only 1 0 value") {
      val dist = createPDist(Array(0.0))
      assert(dist.selectRandomIdx() == 0)
    }
  }

  private def createPDist(a: Array[Double]) = ProbabilityDistribution(a, new Random(1))
}
