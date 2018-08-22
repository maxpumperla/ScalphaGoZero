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
import org.deeplearning4j.nn.modelimport.keras.KerasModelImport
import org.nd4j.linalg.io.ClassPathResource

/**
  * Imports a Keras model for AlphaGoZero
  *
  * @author Max Pumperla
  */
object KerasModel {

  def getDualResnet: ComputationGraph = {
    val file = "dual_res.h5"
    val modelResource = new ClassPathResource(file, this.getClass.getClassLoader)
    KerasModelImport.importKerasModelAndWeights(modelResource.getFile.getAbsolutePath, false)
  }

  def getDualConvnet: ComputationGraph = {
    val file = "dual_conv.h5"
    val modelResource = new ClassPathResource(file, this.getClass.getClassLoader)
    KerasModelImport.importKerasModelAndWeights(modelResource.getFile.getAbsolutePath, false)
  }

  def getSeparateResnets: (ComputationGraph, ComputationGraph) = {
    val policyFile = "sep_res_policy.h5"
    val policyModelResource = new ClassPathResource(policyFile, this.getClass.getClassLoader)
    val policy = KerasModelImport.importKerasModelAndWeights(policyModelResource.getFile.getAbsolutePath, false)

    val valueFile = "sep_res_policy.h5"
    val valueModelResource = new ClassPathResource(valueFile, this.getClass.getClassLoader)
    val value = KerasModelImport.importKerasModelAndWeights(valueModelResource.getFile.getAbsolutePath, false)

    (policy, value)
  }

  def getSeparateConvnets: (ComputationGraph, ComputationGraph) = {
    val policyFile = "sep_conv_policy.h5"
    val policyModelResource = new ClassPathResource(policyFile, this.getClass.getClassLoader)
    val policy = KerasModelImport.importKerasModelAndWeights(policyModelResource.getFile.getAbsolutePath)

    val valueFile = "sep_conv_policy.h5"
    val valueModelResource = new ClassPathResource(valueFile, this.getClass.getClassLoader)
    val value = KerasModelImport.importKerasModelAndWeights(valueModelResource.getFile.getAbsolutePath)

    (policy, value)
  }

}
