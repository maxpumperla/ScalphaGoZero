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

package org.deeplearning4j.scalphagozero.board

/**
  * Go move class. A move can either be playing a point on the board, passing or resigning
  *
  * @param point optional point to be played
  * @param isPass is move a pass?
  * @param isResign is move resignation?
  */
class Move(val point: Option[Point] = None, val isPass: Boolean = false, val isResign: Boolean = false) {

  def isPlay: Boolean = point.isDefined

  override def toString(): String = {
    if (isPass) "pass"
    if (isResign) "resign"
    "row: " + point.get.row + " col: " + point.get.col
  }

}

object Move {

  def play(point: Point): Move = new Move(Some(point))

  def pass(): Move = new Move(None, true, false)

  def resign(): Move = new Move(None, false, true)

}
