package org.deeplearning4j.scalphagozero.agents

import org.deeplearning4j.nn.graph.ComputationGraph
import org.deeplearning4j.scalphagozero.board.{ GameState, Move }
import org.deeplearning4j.scalphagozero.encoders.ZeroEncoder
import org.nd4j.linalg.api.ndarray.INDArray

/**
  * Creates a MCTS node using the model to calculate priors and default value
  * @param model DL4J computation graph suitable for AGZ predictions
  * @param encoder ZeroEncoder instance to feed data into the model
  */
class ZeroTreeNodeCreator(val model: ComputationGraph, val encoder: ZeroEncoder) {

  /**
    * Create a new ZeroTreeNode from the current game state
    * and use the model predictions to initialize the movePriors.
    *
    * @param gameState game state
    * @param move (optional) the move that got us to this state
    * @param parent (optional) parent ZeroTreeNode. No parent if root.
    * @return ZeroTreeNode
    */
  def createNode(gameState: GameState, move: Option[Move] = None, parent: Option[ZeroTreeNode] = None): ZeroTreeNode = {
    val stateTensor: INDArray = encoder.encode(gameState)
    val outputs = model.output(stateTensor)
    val priors = outputs(0).toDoubleVector
    if (priors.exists(p => p.isNaN)) {
      throw new IllegalArgumentException("One of the priors was NaN: " + priors.mkString(", "))
    }
    val value = outputs(1).getDouble(0L, 0L)

    var movePriors: Map[Move, Double] = Map[Move, Double]()
    for (i <- priors.indices) {
      val move = encoder.decodeMoveIndex(i)
      val prior = priors(i)
      movePriors += (move -> prior)
    }

    val newNode = new ZeroTreeNode(gameState, value, movePriors, parent, move)
    if (parent.isDefined && move.isDefined) {
      parent.get.addChild(move.get, newNode)
    }
    newNode
  }
}
