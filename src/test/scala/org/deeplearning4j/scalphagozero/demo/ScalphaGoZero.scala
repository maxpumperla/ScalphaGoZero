package org.deeplearning4j.scalphagozero.demo

import org.deeplearning4j.scalphagozero.agents.ZeroAgent
import org.deeplearning4j.scalphagozero.encoders.ZeroEncoder
import org.deeplearning4j.scalphagozero.experience.ZeroExperienceBuffer
import org.deeplearning4j.scalphagozero.models.DualResnetModel
import org.deeplearning4j.scalphagozero.simulation.ZeroSimulator

object ScalphaGoZero {

  def main(args: Array[String]): Unit = {

    // Define board encoder and model
    val encoder = ZeroEncoder()
    val model = DualResnetModel(20, encoder.numPlanes)

    // Create two AGZ opponents
    val blackAgent = new ZeroAgent(model, encoder)
    val whiteAgent = new ZeroAgent(model, encoder)

    // Run some simulations...
    for (i <- 0 until 2)
      ZeroSimulator.simulateGame(blackAgent, whiteAgent)

    // ... and collect the joint experience
    val experience = ZeroExperienceBuffer.combineExperience(List(blackAgent.getCollector, whiteAgent.getCollector))

    // Use experience data to train one of the agents.
    blackAgent.train(experience)
    System.out.println("Training phase done! You can use black agent to play now")
  }

}
