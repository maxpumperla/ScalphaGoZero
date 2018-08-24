package org.deeplearning4j.scalphagozero.demo

import org.deeplearning4j.scalphagozero.agents.ZeroAgent
import org.deeplearning4j.scalphagozero.encoders.ZeroEncoder
import org.deeplearning4j.scalphagozero.experience.ZeroExperienceBuffer
import org.deeplearning4j.scalphagozero.models.DualResnetModel
import org.deeplearning4j.scalphagozero.simulation.ZeroSimulator

/**
  * Main demo of the project. Creates two opponents, a black and a white
  * AlphaGoZero agent, that play 10 games against each other. The experience
  * data gained from these games is used to train the black agent.
  *
  * For a full-blown AGZ system one would need to continually let opponents
  * play each other, train them, and then start over until they reach
  * sufficient strength. This requires massive amounts of time and compute.
  * So this demo is really just a proof of concept for the AGZ system.
  *
  * @author Max Pumperla
  */
object ScalphaGoZero {

  def main(args: Array[String]): Unit = {

    // Define board encoder and model
    val encoder = ZeroEncoder()
    val model = DualResnetModel(20, encoder.numPlanes)

    // Create two AGZ opponents
    val blackAgent = new ZeroAgent(model, encoder)
    val whiteAgent = new ZeroAgent(model, encoder)

    // Run some simulations...
    for (_ <- 0 until 5)
      ZeroSimulator.simulateGame(blackAgent, whiteAgent)

    // ... and collect the joint experience
    val experience = ZeroExperienceBuffer.combineExperience(List(blackAgent.collector, whiteAgent.collector))

    // Use experience data to train one of the agents.
    blackAgent.train(experience)
    println(">>> Training phase done! You can use black agent to play now")
  }

}
