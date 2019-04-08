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

  def main(args: Array[String]): Unit = {
    val input = new Input()

    // Define board encoder and model
    println("What size go board?[9]")
    val size = input.getNumber(9, 5, 25).toInt
    val encoder = ZeroEncoder(size)
    val model = DualResnetModel(5, encoder.numPlanes, encoder.boardSize)

    // Create two AGZ opponents
    val blackAgent = new ZeroAgent(model, encoder)
    val whiteAgent = new ZeroAgent(model, encoder)

    println("How many episodes should we run for? [5]")
    val episodes = input.getNumber(5, 1, 100).toInt
    println("episodes = " + episodes)

    // Run some simulations...
    for (_ <- 0 until episodes)
      ZeroSimulator.simulateLearningGame(blackAgent, whiteAgent)
    println(">>> Now combining experience from self-play.")

    // ... and collect the joint experience
    val experience = ZeroExperienceBuffer.combineExperience(List(blackAgent.collector, whiteAgent.collector))

    // Use experience data to train one of the agents.
    println(">>> Now using that experience to train the deep neural net.")
    blackAgent.train(experience)

    println(">>> Training phase done! You can use black to play as an AI agent now.")
    val humanAgent = new HumanAgent()
    ZeroSimulator.simulateGame(blackAgent, humanAgent, blackAgent.encoder.boardSize)
  }

}
