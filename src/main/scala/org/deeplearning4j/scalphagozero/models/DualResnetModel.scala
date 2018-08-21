/*
 * Copyright 2016 Skymind
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
