package org.deeplearning4j.scalphagozero.models

import org.deeplearning4j.nn.graph.ComputationGraph

object DualResnetModel {

  def apply(blocks: Int): ComputationGraph = {
    val builder = new DL4JAlphaGoZeroBuilder
    val input = "in"
    builder.addInputs(input)

    val initBlock = "init"
    val convOut = builder.addConvBatchNormBlock(initBlock, input)

    val towerOut = builder.addResidualTower(blocks, convOut)

    val policyOut = builder.addPolicyHead()
    val valueOut = builder.addValueHead()

    builder.addOutputs(List(policyOut, valueOut))

    new ComputationGraph(builder.buildAndReturn())
  }

}
