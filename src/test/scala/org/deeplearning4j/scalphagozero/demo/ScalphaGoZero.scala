package org.deeplearning4j.scalphagozero.demo

import org.deeplearning4j.scalphagozero.agents.ZeroAgent
import org.deeplearning4j.scalphagozero.encoders.ZeroEncoder
import org.deeplearning4j.scalphagozero.experience.{ ZeroExperienceBuffer, ZeroExperienceCollector }
import org.deeplearning4j.scalphagozero.models.DualResnetModel
import org.deeplearning4j.scalphagozero.simulation.Simulator

object ScalphaGoZero {

  def main(args: Array[String]): Unit = {
    val numResidualBlocks = 20

    val encoder = ZeroEncoder()
    val model = DualResnetModel(numResidualBlocks, encoder.numPlanes)
    model.init()

    val blackAgent = new ZeroAgent(model, encoder, roundsPerMove = 10, c = 2.0)
    val whiteAgent = new ZeroAgent(model, encoder, roundsPerMove = 10, c = 2.0)

    val blackCollector = new ZeroExperienceCollector()
    val whiteCollector = new ZeroExperienceCollector()

    blackAgent.setCollector(blackCollector)
    whiteAgent.setCollector(whiteCollector)

    for (i <- 0 until 5)
      Simulator.simulateGame(19, 19, blackAgent, blackCollector, whiteAgent, whiteCollector)

    val experience = ZeroExperienceBuffer.combineExperience(List(blackCollector, whiteCollector))

    blackAgent.train(experience)
    System.out.println("Training phase done! You can use black agent to play now")
  }

}
