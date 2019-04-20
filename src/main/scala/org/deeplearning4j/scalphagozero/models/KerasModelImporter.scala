package org.deeplearning4j.scalphagozero.models

import org.deeplearning4j.nn.graph.ComputationGraph
import org.deeplearning4j.nn.modelimport.keras.KerasModelImport
import org.nd4j.linalg.io.ClassPathResource

/**
  * Imports a Keras model for AlphaGoZero
  *
  * @author Max Pumperla
  */
object KerasModelImporter {

  def getDualResidualNet: ComputationGraph = {
    val file = "dual_res.h5"
    val modelResource = new ClassPathResource(file, this.getClass.getClassLoader)
    KerasModelImport.importKerasModelAndWeights(modelResource.getFile.getAbsolutePath, false)
  }

  def getDualConvolutionNet: ComputationGraph = {
    val file = "dual_conv.h5"
    val modelResource = new ClassPathResource(file, this.getClass.getClassLoader)
    KerasModelImport.importKerasModelAndWeights(modelResource.getFile.getAbsolutePath, false)
  }

  def getSeparateResidualNets: (ComputationGraph, ComputationGraph) = {
    val policyFile = "sep_res_policy.h5"
    val policyModelResource = new ClassPathResource(policyFile, this.getClass.getClassLoader)
    val policy = KerasModelImport.importKerasModelAndWeights(policyModelResource.getFile.getAbsolutePath, false)

    val valueFile = "sep_res_policy.h5"
    val valueModelResource = new ClassPathResource(valueFile, this.getClass.getClassLoader)
    val value = KerasModelImport.importKerasModelAndWeights(valueModelResource.getFile.getAbsolutePath, false)

    (policy, value)
  }

  def getSeparateConvolutionNets: (ComputationGraph, ComputationGraph) = {
    val policyFile = "sep_conv_policy.h5"
    val policyModelResource = new ClassPathResource(policyFile, this.getClass.getClassLoader)
    val policy = KerasModelImport.importKerasModelAndWeights(policyModelResource.getFile.getAbsolutePath)

    val valueFile = "sep_conv_policy.h5"
    val valueModelResource = new ClassPathResource(valueFile, this.getClass.getClassLoader)
    val value = KerasModelImport.importKerasModelAndWeights(valueModelResource.getFile.getAbsolutePath)

    (policy, value)
  }

}
