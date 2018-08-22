package org.deeplearning4j.scalphagozero.models

import org.deeplearning4j.nn.graph.ComputationGraph
import org.scalatest.FunSpec

class TestKerasImport extends FunSpec {

  describe("Testing model import") {
    it("should load the dual resnet model") {
      val model = KerasModel.getDualResnet
      model.summary()
    }

    it("should load the dual convnet model") {
      val model = KerasModel.getDualConvnet
      model.summary()
    }

    it("should load separate convnet models") {
      val model: (ComputationGraph, ComputationGraph) = KerasModel.getSeparateConvnets
      model._1.summary()
      model._2.summary()
    }

    it("should load separate resnet models") {
      val model: (ComputationGraph, ComputationGraph) = KerasModel.getSeparateResnets
      model._1.summary()
      model._2.summary()
    }
  }
}
