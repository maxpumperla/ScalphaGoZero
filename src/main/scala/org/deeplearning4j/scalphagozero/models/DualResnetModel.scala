package org.deeplearning4j.scalphagozero.models

import org.deeplearning4j.nn.graph.ComputationGraph

/**
  * Define and load an AlphaGo Zero dual ResNet architecture
  * into DL4J.
  *
  * @author Max Pumperla
  */
object DualResnetModel {

  def apply(blocks: Int, numPlanes: Int): ComputationGraph = {
    val builder = new DL4JAlphaGoZeroBuilder
    val input = "in"

    builder.addInputs(input)
    val initBlock = "init"
    val convOut = builder.addConvBatchNormBlock(initBlock, input, numPlanes)
    val towerOut: String = builder.addResidualTower(blocks, convOut)
    val policyOut = builder.addPolicyHead(towerOut)
    val valueOut = builder.addValueHead(towerOut)
    builder.addOutputs(List(policyOut, valueOut))

    val model = new ComputationGraph(builder.buildAndReturn())
    model.init()

    model
  }

}
