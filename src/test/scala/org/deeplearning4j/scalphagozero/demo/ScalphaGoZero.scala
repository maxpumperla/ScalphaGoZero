package org.deeplearning4j.scalphagozero.demo

import org.deeplearning4j.nn.conf.{ComputationGraphConfiguration, NeuralNetConfiguration}
import org.deeplearning4j.nn.graph.ComputationGraph
import org.deeplearning4j.scalphagozero.agents.ZeroAgent
import org.deeplearning4j.scalphagozero.encoders.ZeroEncoder
import org.deeplearning4j.scalphagozero.experience.{ZeroExperienceBuffer, ZeroExperienceCollector}
import org.deeplearning4j.scalphagozero.simulation.Simulator

object ScalphaGoZero {

  def main(args: Array[String]): Unit = {
    val boardSize = 19
    val encoder = ZeroEncoder(boardSize, boardSize)

    // TODO: get from models module
    val config: ComputationGraphConfiguration = new NeuralNetConfiguration.Builder()
      .graphBuilder()
      .build()

    val model = new ComputationGraph(config)

    val blackAgent = new ZeroAgent(model, encoder, roundsPerMove = 10, c = 2.0)
    val whiteAgent = new ZeroAgent(model, encoder, roundsPerMove = 10, c = 2.0)

    val blackCollector = new ZeroExperienceCollector()
    val whiteCollector = new ZeroExperienceCollector()

    blackAgent.setCollector(blackCollector)
    whiteAgent.setCollector(whiteCollector)

    for (i <- 0 until 5)
      Simulator.simulateGame(boardSize, boardSize, blackAgent, blackCollector, whiteAgent, whiteCollector)

    val experience = ZeroExperienceBuffer.combineExperience(List(blackCollector, whiteCollector))
    blackAgent.train(experience)
  }

}
