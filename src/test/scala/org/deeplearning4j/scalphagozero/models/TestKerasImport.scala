package org.deeplearning4j.scalphagozero.models

import org.deeplearning4j.nn.graph.ComputationGraph
import org.scalatest.FunSpec

class TestKerasImport extends FunSpec {

  describe("Testing model import") {
    ignore("should load the dual resnet model") {
      val model = KerasModel.getDualResidualNet
      model.summary()
    }

    ignore("should load the dual convnet model") {
      val model = KerasModel.getDualConvolutionNet
      model.summary()
    }

    // TODO: this fails "expectedly" on DL4J beta2. wait for new release to reactivate.
    ignore("should load separate convnet models") {
      val model: (ComputationGraph, ComputationGraph) = KerasModel.getSeparateConvolutionNets
      model._1.summary()
      model._2.summary()
    }

    ignore("should load separate resnet models") {
      val model: (ComputationGraph, ComputationGraph) = KerasModel.getSeparateResidualNets
      model._1.summary()
      model._2.summary()
    }
  }
}
