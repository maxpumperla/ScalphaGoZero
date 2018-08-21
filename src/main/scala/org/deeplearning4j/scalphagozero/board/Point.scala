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
  * Go board point class.
  *
  * @param row row index
  * @param col column index
  *
  * @author Max Pumperla
  */
class Point(val row: Int, val col: Int) {

  def neighbors: List[Point] = List(
    Point(this.row - 1, this.col),
    Point(this.row + 1, this.col),
    Point(this.row, this.col - 1),
    Point(this.row, this.col + 1)
  )

  override def equals(obj: scala.Any): Boolean = {
    if (obj.isInstanceOf[Point]) {
      val castPoint = obj.asInstanceOf[Point]
      return this.row == castPoint.row && this.col == castPoint.col
    }
    false
  }

  def toCoords: (Int, Int) = (this.row, this.col)

}

/**
  * Go board point object
  *
  * @author Max Pumperla
  */
object Point {

  def apply(row: Int, col: Int): Point = new Point(row, col)

}
