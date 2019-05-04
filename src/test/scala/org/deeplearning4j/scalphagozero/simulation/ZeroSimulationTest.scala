package org.deeplearning4j.scalphagozero.simulation

import java.io.File
import org.deeplearning4j.nn.graph.ComputationGraph
import org.deeplearning4j.scalphagozero.agents.ZeroAgent
import org.deeplearning4j.scalphagozero.encoders.ZeroEncoder
import org.nd4j.linalg.api.ndarray.INDArray
import org.scalatest.FunSpec
import ZeroSimulationTest.RND
import org.deeplearning4j.scalphagozero.{ PATH_PREFIX, strip }
import scala.collection.mutable.ListBuffer
import scala.util.Random

/**
  * @author Barry Becker
  */
class ZeroSimulationTest extends FunSpec {

  describe("A ZeroAgent selecting a move") {

    val model: ComputationGraph =
      ComputationGraph.load(new File(PATH_PREFIX + "model_size_5_layers_2.model"), true)

    val encoder = new ZeroEncoder(5)
    val blackAgent = new ZeroAgent(model, encoder, roundsPerMove = 20, c = 2.0, rand = RND)
    val whiteAgent = new ZeroAgent(model, encoder, roundsPerMove = 20, c = 2.0, rand = RND)
    ZeroSimulator.simulateLearningGame(blackAgent, whiteAgent)

    // check rewards, states, visitCounts
    it("black collector should have these visitCounts, rewards, and states") {
      val vc: ListBuffer[INDArray] = blackAgent.collector.visitCounts
      assert(vc.size == 21)
      println(vc.mkString(", "))
      assertResult(
        strip(
          """[[    1.0000,    1.0000,         0,         0,         0,    1.0000,         0,    1.0000,    1.0000,    1.0000,    1.0000,    1.0000,    1.0000,    1.0000,    1.0000,    1.0000,    1.0000,         0,    1.0000,    1.0000,    1.0000,    1.0000,    1.0000,    1.0000,    1.0000,         0]], [[    1.0000,    1.0000,    1.0000,         0,         0,    1.0000,         0,    1.0000,    1.0000,    1.0000,    1.0000,    1.0000,    1.0000,    1.0000,    1.0000,    1.0000,    1.0000,    1.0000,    1.0000,         0,    1.0000,    1.0000,    1.0000,    1.0000,         0,         0]], [[    1.0000,    1.0000,    1.0000,         0,    1.0000,         0,    1.0000,    1.0000,         0,    1.0000,    1.0000,    1.0000,    1.0000,    1.0000,    1.0000,    1.0000,    1.0000,    1.0000,    1.0000,         0,    1.0000,    1.0000,    1.0000,    1.0000,         0,         0]], [[         0,    1.0000,         0,    1.0000,    1.0000,         0,    1.0000,    1.0000,         0,    1.0000,    1.0000,    1.0000,    1.0000,    1.0000,    1.0000,    1.0000,    1.0000,    1.0000,    1.0000,         0,    1.0000,    1.0000,    2.0000,    1.0000,         0,         0]], [[         0,    1.0000,         0,    1.0000,    1.0000,         0,    1.0000,    1.0000,         0,    4.0000,    1.0000,         0,    1.0000,    1.0000,    1.0000,    1.0000,    1.0000,    1.0000,    1.0000,         0,    1.0000,    1.0000,         0,    1.0000,         0,         0]], [[         0,    1.0000,         0,    1.0000,    1.0000,         0,    1.0000,    1.0000,         0,         0,    1.0000,         0,    1.0000,    6.0000,    1.0000,    1.0000,    1.0000,    1.0000,         0,         0,    1.0000,    1.0000,         0,    1.0000,         0,         0]], [[         0,         0,         0,    1.0000,    1.0000,         0,    1.0000,    1.0000,         0,         0,    1.0000,         0,    1.0000,         0,    1.0000,    1.0000,    1.0000,    1.0000,         0,         0,    8.0000,    1.0000,         0,    1.0000,         0,         0]], [[         0,         0,         0,    1.0000,    1.0000,         0,    1.0000,    1.0000,         0,         0,         0,         0,    1.0000,         0,    1.0000,    1.0000,   10.0000,    1.0000,         0,         0,         0,    1.0000,         0,    1.0000,         0,         0]], [[         0,         0,         0,    1.0000,    1.0000,         0,    1.0000,    1.0000,         0,         0,         0,         0,    1.0000,         0,    1.0000,         0,         0,    1.0000,         0,         0,         0,   12.0000,         0,    1.0000,         0,         0]], [[         0,         0,         0,    1.0000,    1.0000,         0,    1.0000,    1.0000,         0,         0,         0,         0,         0,         0,   14.0000,         0,         0,    1.0000,         0,         0,         0,         0,         0,    1.0000,         0,         0]], [[         0,         0,         0,    1.0000,    1.0000,         0,    1.0000,   17.0000,         0,         0,         0,         0,         0,         0,         0,         0,         0,         0,         0,         0,         0,         0,         0,         0,         0,         0]], [[         0,         0,         0,    1.0000,    1.0000,         0,         0,         0,         0,         0,         0,         0,         0,         0,         0,         0,    1.0000,         0,         0,         0,    1.0000,    1.0000,   15.0000,         0,         0,         0]], [[         0,         0,         0,    1.0000,    1.0000,         0,         0,         0,         0,         0,         0,         0,         0,         0,         0,         0,   17.0000,         0,         0,         0,         0,    1.0000,         0,         0,         0,         0]], [[         0,         0,         0,    1.0000,   19.0000,         0,         0,         0,         0,         0,         0,         0,         0,         0,         0,         0,         0,         0,         0,         0,         0,         0,         0,         0,         0,         0]], [[   19.0000,         0,         0,         0,         0,    1.0000,         0,         0,         0,         0,         0,         0,         0,         0,         0,         0,         0,         0,         0,         0,         0,         0,         0,         0,         0,         0]], [[         0,         0,         0,         0,         0,         0,         0,         0,         0,         0,         0,         0,         0,         0,         0,         0,         0,         0,         0,         0,         0,         0,         0,         0,         0,   20.0000]], [[         0,         0,         0,         0,         0,         0,         0,         0,         0,         0,         0,         0,         0,         0,         0,         0,         0,         0,         0,         0,         0,         0,         0,         0,         0,   20.0000]], [[         0,         0,         0,         0,    1.0000,         0,         0,         0,         0,   17.0000,         0,         0,         0,    1.0000,    1.0000,         0,         0,         0,         0,         0,         0,         0,         0,         0,         0,         0]], [[         0,         0,         0,         0,    8.0000,         0,         0,         0,         0,         0,         0,         0,         0,         0,   12.0000,         0,         0,         0,         0,         0,         0,         0,         0,         0,         0,         0]], [[         0,         0,         0,         0,         0,         0,         0,         0,         0,   10.0000,         0,         0,         0,         0,   10.0000,         0,         0,         0,         0,         0,         0,         0,         0,         0,         0,         0]], [[         0,         0,         0,         0,         0,         0,         0,         0,         0,         0,         0,         0,         0,         0,         0,         0,         0,         0,         0,         0,         0,         0,         0,         0,         0,   20.0000]]"""
        )
      ) {
        vc.mkString(", ")
      }

      val rewards: ListBuffer[INDArray] = blackAgent.collector.rewards
      assertResult(
        strip(
          """-1.0000, -1.0000, -1.0000, -1.0000, -1.0000, -1.0000, -1.0000, -1.0000, -1.0000, -1.0000, -1.0000, -1.0000, -1.0000, -1.0000, -1.0000, -1.0000, -1.0000, -1.0000, -1.0000, -1.0000, -1.0000"""
        )
      ) {
        rewards.mkString(", ")
      }
    }

    it("white collector should have these visitCounts, rewards, and states") {
      // check rewards states, visitCounts
      val rewards: ListBuffer[INDArray] = whiteAgent.collector.rewards
      assertResult(
        strip(
          """1.0000, 1.0000, 1.0000, 1.0000, 1.0000, 1.0000, 1.0000, 1.0000, 1.0000, 1.0000, 1.0000, 1.0000, 1.0000, 1.0000, 1.0000, 1.0000, 1.0000, 1.0000, 1.0000, 1.0000, 1.0000"""
        )
      ) {
        rewards.mkString(", ")
      }
    }
  }
}

object ZeroSimulationTest {
  val RND = new Random(1)
}
