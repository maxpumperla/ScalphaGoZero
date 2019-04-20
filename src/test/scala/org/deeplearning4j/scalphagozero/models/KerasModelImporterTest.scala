package org.deeplearning4j.scalphagozero.models

import org.deeplearning4j.nn.graph.ComputationGraph
import org.scalatest.FunSpec

class KerasModelImporterTest extends FunSpec {

  describe("Testing model import") {
    it("should load the dual resnet model") {
      val model = KerasModelImporter.getDualResidualNet
      model.summary()
    }

    it("should load the dual convnet model") {
      val model = KerasModelImporter.getDualConvolutionNet
      model.summary()
    }

    // This now works using DL4J-beta3.
    it("should load separate convnet models") {
      val model: (ComputationGraph, ComputationGraph) = KerasModelImporter.getSeparateConvolutionNets
      model._1.summary()
      model._2.summary()
    }

    it("should load separate resnet models") {
      val model: (ComputationGraph, ComputationGraph) = KerasModelImporter.getSeparateResidualNets
      model._1.summary()
      model._2.summary()
    }
  }
}
