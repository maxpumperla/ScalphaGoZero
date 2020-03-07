package org.deeplearning4j.scalphagozero.app

import org.deeplearning4j.scalphagozero.agents.ZeroAgent
import org.deeplearning4j.scalphagozero.experience.ZeroExperienceBuffer
import org.deeplearning4j.scalphagozero.simulation.ZeroSimulator
import Trainer.DEFAULT_BATCH_SIZE

/**
  * Run a bunch of simulations/episodes in batches, and train the model for black using what is learned from self-play.
  * @param batchSize the number of games to run before updating the model with that new knowledge.
  * @author Barry Becker
  * @author Max Pumperla
  */
case class Trainer(batchSize: Int = DEFAULT_BATCH_SIZE) {

  /** Play game simulations in batches, and train the black model incrementally after each batch of simulated games. */
  def runSimulationsAndTrain(episodes: Int, blackAgent: ZeroAgent, whiteAgent: ZeroAgent): Unit = {

    var simulationTime: Long = 0
    var trainingTime: Long = 0

    for (i <- 1 to episodes) {
      println("episode " + i)
      val startSim = System.currentTimeMillis()
      ZeroSimulator.simulateLearningGame(blackAgent, whiteAgent)
      simulationTime += (System.currentTimeMillis() - startSim)

      if (i % batchSize == 0) {
        trainingTime += train(blackAgent, whiteAgent)
      }
    }

    if (episodes % batchSize != 0) {
      trainingTime += train(blackAgent, whiteAgent) // train based on those leftover at the end
    }

    printPerformance(simulationTime, trainingTime, episodes)
  }

  /** @return time in seconds spent training */
  private def train(blackAgent: ZeroAgent, whiteAgent: ZeroAgent): Long = {
    val startTraining = System.currentTimeMillis()

    // ... and collect the joint experience
    println(">>> Now combining experience from self-play.")
    val experience = ZeroExperienceBuffer.combineExperience(
      List(
        blackAgent.retrieveAndClearCollector(),
        whiteAgent.retrieveAndClearCollector()
      )
    )

    // Use experience data to train one of the agents.
    println(">>> Now using that experience to train the deep neural net for black.")
    blackAgent.train(experience)

    System.currentTimeMillis() - startTraining
  }

  /** Print statistics that can be added to /performance/performance-results.csv */
  private def printPerformance(simulationTime: Long, trainingTime: Long, numEpisodes: Int): Unit = {
    println()
    println("Consider entering the following information on a new row in /performance/performance-results.csv")

    println("OS: " + System.getProperty("os.name"))
    println("ProcessorId: " + System.getenv("PROCESSOR_IDENTIFIER"))
    println("ProcessorArch: " + System.getenv("PROCESSOR_ARCHITECTURE"))
    println("Available processors (cores): " + Runtime.getRuntime.availableProcessors)
    println("Total memory available to JVM (mega-bytes): " + Runtime.getRuntime.totalMemory / 1000000)
    println("Time (in seconds) spent running simulations: " + (simulationTime / 1000).toInt)
    println("Time (in seconds) spent running training: " + (trainingTime / 1000).toInt)
    if (numEpisodes > 0)
      println("Average seconds per episode: " + (simulationTime / numEpisodes / 1000).toInt)
    println()
  }
}

object Trainer {
  val DEFAULT_BATCH_SIZE = 5
}
