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
  * A Go string is a collection of stones of given color and corresponding liberties
  *
  * @param color stone color
  * @param stones stones of the Go string
  * @param liberties liberties of the Go string
  *
  * @author Max Pumperla
  */
class GoString(val color: Int, var stones: Set[(Int, Int)], var liberties: Set[(Int, Int)]) {

  def numLiberties: Int = liberties.size

  def withoutLiberty(point: Point): GoString = {
    val newLiberties = this.liberties - point.toCoords
    GoString(this.color, this.stones, newLiberties)
  }

  def withLiberty(point: Point): GoString = {
    val newLiberties = this.liberties + point.toCoords
    GoString(this.color, this.stones, newLiberties)
  }

  def mergedWith(goString: GoString): GoString = {
    if (!color.equals(goString.color))
      throw new IllegalArgumentException("Color of Go strings has to match")
    val combinedStones = stones ++ goString.stones
    val commonLiberties = (liberties ++ goString.liberties) -- combinedStones
    GoString(color, combinedStones, commonLiberties)
  }

  override def equals(obj: scala.Any): Boolean = {
    if (obj.isInstanceOf[GoString]) {
      val castObj = obj.asInstanceOf[GoString]
      return this.color.equals(castObj.color) && this.stones.equals(castObj.stones) && this.liberties
        .equals(castObj.liberties)

    }
    return false
  }
}

object GoString {

  def apply(color: Int, stones: Set[(Int, Int)], liberties: Set[(Int, Int)]): GoString =
    new GoString(color, stones, liberties)
}
