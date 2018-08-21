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

package org.deeplearning4j.scalphagozero.encoders

import org.deeplearning4j.scalphagozero.board.{ GameState, Move, Point }
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
    * Encodes a given move as board index
    *
    * @param move Go Move instance
    * @return board index of move
    */
  def encodeMove(move: Move): Int

  /**
    * Retrieves a move from a board index
    *
    * @param index board index of a move
    * @return Move instance corresponding to index
    */
  def decodeMoveIndex(index: Int): Move

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
