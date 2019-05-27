package org.deeplearning4j.scalphagozero.models

import org.deeplearning4j.nn.graph.ComputationGraph

/**
  * Define and load an AlphaGo Zero dual ResNet architecture
  * into DL4J.
  *
  * The dual residual architecture is the strongest
  * of the architectures tested by DeepMind for AlphaGo
  * Zero. It consists of an initial convolution layer block,
  * followed by a number (40 for the strongest, 20 as
  * baseline) of residual blocks. The network is topped
  * off by two "heads", one to predict policies and one
  * for value functions.
  *
  * @author Max Pumperla
  */
object DualResnetModel {

  def apply(numBlocks: Int, numPlanes: Int, boardSize: Int): ComputationGraph = {
    val builder = new DL4JAlphaGoZeroBuilder(boardSize)
    val input = "in"

    builder.addInputs(input)
    val initBlock = "init"
    val convOut = builder.addConvBatchNormBlock(initBlock, input, numPlanes)
    val towerOut: String = builder.addResidualTower(numBlocks, convOut)
    val policyOut = builder.addPolicyHead(towerOut)
    val valueOut = builder.addValueHead(towerOut)
    builder.addOutputs(List(policyOut, valueOut))

    val model = new ComputationGraph(builder.buildAndReturn())
    model.init()

    model
  }

}
