package org.deeplearning4j.scalphagozero.encoders

import org.deeplearning4j.scalphagozero.board.{ GameState, Point }
import org.nd4j.linalg.api.ndarray.INDArray

/**
  * Base trait for Go board encoders. An encoder translates game state information
  * into ND4J tensors that can be fed into neural networks.
  *
  * @author Max Pumperla
  */
abstract class Encoder(val boardHeight: Int = 19, val boardWidth: Int = 19, val numPlanes: Int) {

  /**
    * Name of the encoder.
    *
    * @return Encoder name as String
    */
  def name(): String

  /**
    * Encode the current game state as board tensor
    *
    * @param gameState GameState instance
    * @return Board tensor representation of the game state
    */
  def encode(gameState: GameState): INDArray

  /**
    * Turn a board point to an integer index.
    *
    * @param point Board point
    * @return Index representation of the point
    */
  def pointToIndex(point: Point): Int =
    boardWidth * (point.row) + point.col

  /**
    * Decodes an index back into a point representation.
    *
    * @param index index of a point
    * @return Board point corresponding to index.
    */
  def indexToPoint(index: Int): Point = {
    val row = index / boardWidth
    val col = index % boardWidth
    new Point(row + 1, col + 1)
  }

  /**
    * Get number of points on the board.
    *
    * @return Number of board points
    */
  def numberOfPoints(): Int = boardWidth * boardHeight

  /**
    * Shape of encoded tensors
    *
    * @return Tensor shape as array
    */
  def shape(): Array[Int] = Array(numPlanes, boardHeight, boardWidth)
}
