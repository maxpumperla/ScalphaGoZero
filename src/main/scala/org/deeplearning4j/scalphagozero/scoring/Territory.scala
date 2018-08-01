package org.deeplearning4j.scalphagozero.scoring

import java.util

import org.deeplearning4j.scalphagozero.board.Point

/**
  * Class to track territory on the Go board
  *
  * @param territoryMap map of Go points to status codes
  *
  * @author Max Pumperla
  */
class Territory(territoryMap: util.HashMap[Point, String]) {

  private var numBlackTerritory = 0
  private var numWhiteTerritory = 0
  private var numBlackStones = 0
  private var numWhiteStones = 0
  private var numDame = 0
  private var damePoints: util.ArrayList[Point] = new util.ArrayList[Point]()

  for (point <- territoryMap.keySet()) {
    val status = territoryMap(point)
    status match {
      case "black" => numBlackStones += 1
      case "white" => numWhiteStones += 1
      case "territory_b" => numBlackTerritory += 1
      case "territory_w" => numWhiteTerritory += 1
      case "dame" =>
        numDame += 1
        damePoints.add(point)
    }
  }
}
