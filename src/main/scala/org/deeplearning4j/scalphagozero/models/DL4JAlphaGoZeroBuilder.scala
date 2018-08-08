package org.deeplearning4j.scalphagozero.models

import org.deeplearning4j.nn.conf.graph.ElementWiseVertex
import org.deeplearning4j.nn.conf.graph.ElementWiseVertex.Op
import org.deeplearning4j.nn.conf.layers._
import org.deeplearning4j.nn.conf.{ComputationGraphConfiguration, ConvolutionMode, NeuralNetConfiguration}
import org.deeplearning4j.nn.weights.WeightInit
import org.nd4j.linalg.activations.Activation
import org.nd4j.linalg.learning.config.Sgd

class DL4JAlphaGoZeroBuilder {

  val conf: ComputationGraphConfiguration.GraphBuilder = new NeuralNetConfiguration.Builder()
    .updater(new Sgd()).weightInit(WeightInit.LECUN_NORMAL)
    .graphBuilder()

  def addInputs(name:String): Unit = {
    conf.addInputs(name)
  }

  def addOutputs(names: List[String]): Unit = {
    conf.setOutputs(names:_*)
  }

  def buildAndReturn(): ComputationGraphConfiguration = conf.build()

  def addConvBatchNormBlock(blockName: String, inName: String, useActivation: Boolean = true, filters: Int = 256,
                            kernelSize: List[Int] =List(3, 3), strides: List[Int] =List(1, 1),
                            convolutionMode: ConvolutionMode = ConvolutionMode.Same): String = {
    val convName = "conv_"+ blockName
    val bnName = "batch_norm_" + blockName
    val actName = "relu_" + blockName

    conf.addLayer(convName, new ConvolutionLayer.Builder()
      .kernelSize(kernelSize:_*).stride(strides:_*).convolutionMode(convolutionMode).nOut(filters).build(), inName)
    conf.addLayer(bnName, new BatchNormalization.Builder().build(), convName)
    if (useActivation) {
      conf.addLayer(actName, new ActivationLayer.Builder().activation(Activation.RELU).build(), bnName)
      actName
    } else
      bnName
  }

  def addResidualBlock(blockNumber: Int, inName: String, filters: Int = 256,
                       kernelSize: List[Int] =List(3, 3), strides: List[Int] =List(1, 1),
                       convolutionMode: ConvolutionMode = ConvolutionMode.Same): String = {
    val firstBlock = "residual_1_" + blockNumber
    val firstOut = "relu_residual_1_" + blockNumber
    val secondBlock = "residual_2_" + blockNumber
    val mergeBlock = "add_" + blockNumber
    val actBlock = "relu_" + blockNumber

    addConvBatchNormBlock(firstBlock, inName, true, filters, kernelSize, strides, convolutionMode)
    addConvBatchNormBlock(secondBlock, firstOut , false, filters, kernelSize, strides, convolutionMode)
    conf.addVertex(mergeBlock, new ElementWiseVertex(Op.Add), firstBlock, secondBlock)
    conf.addLayer(actBlock, new ActivationLayer.Builder().activation(Activation.RELU).build(), mergeBlock)
    actBlock
  }

  def addResidualTower(numBlocks: Int, inName:String,  filters: Int = 256,
                    kernelSize: List[Int] =List(3, 3), strides: List[Int] =List(1, 1),
                    convolutionMode: ConvolutionMode = ConvolutionMode.Same): String = {
    var name = inName
    for (i <- 0 until numBlocks)
      name = addResidualBlock(i, name, filters, kernelSize, strides, convolutionMode)
    name
  }

  def addConvolutionalTower(numBlocks: Int, inName:String,  filters: Int = 256,
                       kernelSize: List[Int] =List(3, 3), strides: List[Int] =List(1, 1),
                       convolutionMode: ConvolutionMode  = ConvolutionMode.Same): Unit = {
    var name = inName
    for (i <- 0 until numBlocks)
      name = addConvBatchNormBlock(i.toString, name, true, filters, kernelSize, strides, convolutionMode)
  }

  def addPolicyHead(): String = "policy" // TODO
  def addValueHead(): String = "value" // TODO


}
