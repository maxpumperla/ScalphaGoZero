package org.deeplearning4j.scalphagozero.app

import java.io.File

import org.deeplearning4j.nn.conf.CacheMode
import org.deeplearning4j.nn.graph.ComputationGraph
import org.deeplearning4j.scalphagozero.agents.{ HumanAgent, ZeroAgent }
import org.deeplearning4j.scalphagozero.encoders.ZeroEncoder
import org.deeplearning4j.scalphagozero.input.Input
import org.deeplearning4j.scalphagozero.models.DualResnetModel
import org.deeplearning4j.scalphagozero.simulation.ZeroSimulator
import org.deeplearning4j.scalphagozero.util.ObjectSizer

import scala.util.Random

/**
  * Main demo of the project. Creates two opponents, a black and a white
  * AlphaGoZero agent, that play some number of games against each other. The experience
  * gained from these games is used to train the black agent.
  *
  * For a full-blown AGZ system, one would need to continually let opponents
  * play each other in order to train them until they reach sufficient strength.
  * This requires massive amounts of time and compute.
  * The model is updated after a batch of games is played.
  *
  * @author Max Pumperla
  * @author Barry Becker
  */
class Demo() {

  val MODELS_PATH = "models/"
  val input = Input()

  def run(): Unit = {

    // Define board encoder and model
    val size = input.getInteger("What size go board?", 9, 3, 25)
    val numLayers = input.getInteger("How many residual blocks to use?", 5, 1, 40)
    val encoder = ZeroEncoder(size)
    val model = getModel(numLayers, encoder)
    println("The initial size of the model is: " + ObjectSizer.getSizeKB(model))
    val roundsPerMove = input.getInteger("How many rounds of MC playouts per move?", 100, 1, 1000)

    // Create two AGZ opponents based on the same model
    val rnd = new Random(1)
    val blackAgent = new ZeroAgent(model, encoder, roundsPerMove, rand = rnd)
    val whiteAgent = new ZeroAgent(model, encoder, roundsPerMove, rand = rnd)

    // Run some simulations...
    val episodes = input.getInteger("How many episodes should we run for?", 5, 0, 3000)
    val batchSize = input.getInteger("What batch size?", 5, 2, 100)
    val trainer = Trainer(batchSize)
    trainer.runSimulationsAndTrain(episodes, blackAgent, whiteAgent)

    println(">>> Training phase done! You can use black to play as an AI agent now.\n")
    if (episodes > 0)
      optionallySaveModel(blackAgent.model, size, numLayers)

    val humanAgent = new HumanAgent()
    val komi = input.getInteger("How much komi for white? 0.5 +", 6, 0, 7)
    ZeroSimulator.simulateGame(blackAgent, humanAgent, blackAgent.encoder.boardSize, komi + 0.5f)
  }

  /** Either a new DualResnetModel or one that has been pre-trained */
  private def getModel(numLayers: Int, encoder: ZeroEncoder): ComputationGraph = {
    val c: Character =
      input.charQuery("Do you want to load pre-trained model?", Seq('y', 'n'), Some('y'))
    if (c.toString.toUpperCase() == "Y") {
      val fname = input.textQuery(
        "Load from which file?",
        s"${MODELS_PATH}model_size_${encoder.boardSize}_layers_$numLayers.model"
      )
      val graph = ComputationGraph.load(new File(fname), true)
      graph.setCacheMode(CacheMode.HOST)
      graph
    } else DualResnetModel(numLayers, encoder.numPlanes, encoder.boardSize)
  }

  private def optionallySaveModel(model: ComputationGraph, size: Int, numLayers: Int): Unit = {
    val c: Character =
      input.charQuery("Do you want to first save the result of this model?", Seq('y', 'n'), Some('y'))
    if (c.toString.toUpperCase() == "Y") {
      val fname = input.textQuery("Save to which file?", s"${MODELS_PATH}model_size_${size}_layers_$numLayers.model")
      model.save(new File(fname))
    }
  }
}
