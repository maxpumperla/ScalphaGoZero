package org.deeplearning4j.scalphagozero.demo

import org.deeplearning4j.scalphagozero.agents.{ HumanAgent, ZeroAgent }
import org.deeplearning4j.scalphagozero.encoders.ZeroEncoder
import org.deeplearning4j.scalphagozero.experience.ZeroExperienceBuffer
import org.deeplearning4j.scalphagozero.models.DualResnetModel
import org.deeplearning4j.scalphagozero.simulation.ZeroSimulator
import org.deeplearning4j.scalphagozero.util.Input

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

  val input = new Input()

  def main(args: Array[String]): Unit = {

    // Define board encoder and model
    val size = input.getInteger("What size go board?", 9, 5, 25)
    val numLayers = input.getInteger("How many residual blocks to use?", 5, 1, 40)
    val encoder = ZeroEncoder(size)
    val model = DualResnetModel(numLayers, encoder.numPlanes, encoder.boardSize)

    // Create two AGZ opponents
    val blackAgent = new ZeroAgent(model, encoder)
    val whiteAgent = new ZeroAgent(model, encoder)

    // Run some simulations...
    val episodes = input.getInteger("How many episodes should we run for?", 5, 1, 10000)
    for (i <- 0 until episodes) {
      println("episode " + i)
      ZeroSimulator.simulateLearningGame(blackAgent, whiteAgent)
    }

    // ... and collect the joint experience
    println(">>> Now combining experience from self-play.")
    val experience = ZeroExperienceBuffer.combineExperience(List(blackAgent.collector, whiteAgent.collector))

    // Use experience data to train one of the agents.
    println(">>> Now using that experience to train the deep neural net.")
    blackAgent.train(experience)

    println(">>> Training phase done! You can use black to play as an AI agent now.\n")
    val humanAgent = new HumanAgent()
    ZeroSimulator.simulateGame(blackAgent, humanAgent, blackAgent.encoder.boardSize)
  }
}
