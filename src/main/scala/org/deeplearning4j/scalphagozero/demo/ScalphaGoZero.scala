package org.deeplearning4j.scalphagozero.demo

import java.io.File

import org.deeplearning4j.nn.graph.ComputationGraph
import org.deeplearning4j.scalphagozero.agents.{ HumanAgent, ZeroAgent }
import org.deeplearning4j.scalphagozero.encoders.ZeroEncoder
import org.deeplearning4j.scalphagozero.experience.ZeroExperienceBuffer
import org.deeplearning4j.scalphagozero.models.DualResnetModel
import org.deeplearning4j.scalphagozero.simulation.ZeroSimulator
import org.deeplearning4j.scalphagozero.util.Input

import scala.util.Random

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
  val BATCH_SIZE = 5

  def main(args: Array[String]): Unit = {

    // Define board encoder and model
    val size = input.getInteger("What size go board?", 9, 5, 25)
    val numLayers = input.getInteger("How many residual blocks to use?", 5, 1, 40)
    val encoder = ZeroEncoder(size)
    val model = getModel(numLayers, encoder)

    // Create two AGZ opponents
    val rnd = new Random(1)
    val blackAgent = new ZeroAgent(model, encoder, rand = rnd)
    val whiteAgent = new ZeroAgent(model, encoder, rand = rnd)

    // Run some simulations...
    val episodes = input.getInteger("How many episodes should we run for?", 5, 1, 3000)

    runSimulationsAndTrain(episodes, blackAgent, whiteAgent)

    println(">>> Training phase done! You can use black to play as an AI agent now.\n")
    optionallySaveModel(blackAgent.model, size, numLayers)

    val humanAgent = new HumanAgent()
    ZeroSimulator.simulateGame(blackAgent, humanAgent, blackAgent.encoder.boardSize)
  }

  private def runSimulationsAndTrain(episodes: Int, blackAgent: ZeroAgent, whiteAgent: ZeroAgent): Unit = {

    for (i <- 1 to episodes) {
      println("episode " + i)
      ZeroSimulator.simulateLearningGame(blackAgent, whiteAgent)

      if (i % BATCH_SIZE == 0)
        train(blackAgent, whiteAgent)
    }

    if (episodes % BATCH_SIZE != 0)
      train(blackAgent, whiteAgent) // train based on those leftover at the end
  }

  private def train(blackAgent: ZeroAgent, whiteAgent: ZeroAgent): Unit = {
    // ... and collect the joint experience
    println(">>> Now combining experience from self-play.")
    val experience = ZeroExperienceBuffer.combineExperience(List(blackAgent.collector, whiteAgent.collector))

    // Use experience data to train one of the agents.
    println(">>> Now using that experience to train the deep neural net for black.")
    blackAgent.train(experience)
  }

  /** Either a new DualResnetModel or one that has been pre-trained */
  private def getModel(numLayers: Int, encoder: ZeroEncoder): ComputationGraph = {
    val c: Character =
      input.charQuery("Do you want to load pre-trained model?", Seq('y', 'n'), Some('y'))
    if (c.toString.toUpperCase() == "Y") {
      val fname = input.textQuery("Load from which file?", s"model_size_${encoder.boardSize}_layers_$numLayers.model")
      ComputationGraph.load(new File(fname), true)
    } else DualResnetModel(numLayers, encoder.numPlanes, encoder.boardSize)
  }

  private def optionallySaveModel(model: ComputationGraph, size: Int, numLayers: Int): Unit = {
    val c: Character =
      input.charQuery("Do you want to first save the result of this model?", Seq('y', 'n'), Some('y'))
    if (c.toString.toUpperCase() == "Y") {
      val fname = input.textQuery("Save to which file?", s"model_size_${size}_layers_$numLayers.model")
      model.save(new File(fname))
    }
  }
}
