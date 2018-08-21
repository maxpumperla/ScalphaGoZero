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

package org.deeplearning4j.scalphagozero.scoring

import java.util

import org.deeplearning4j.scalphagozero.board.Point

import scala.collection.mutable

/**
  * Class to track territory on the Go board
  *
  * @param territoryMap map of Go points to status codes
  *
  * @author Max Pumperla
  */
class Territory(territoryMap: mutable.HashMap[Point, String]) {

  var numBlackTerritory = 0
  var numWhiteTerritory = 0
  var numBlackStones = 0
  var numWhiteStones = 0
  var numDame = 0
  private var damePoints: util.ArrayList[Point] = new util.ArrayList[Point]()

  for (point <- territoryMap.keys) {
    val status = territoryMap(point)
    status match {
      case "black"       => numBlackStones += 1
      case "white"       => numWhiteStones += 1
      case "territory_b" => numBlackTerritory += 1
      case "territory_w" => numWhiteTerritory += 1
      case "dame" =>
        numDame += 1
        damePoints.add(point)
      case _ => throw new IllegalArgumentException("Unsupported status type:" + status)
    }
  }
}
