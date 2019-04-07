package org.deeplearning4j.scalphagozero.scoring

import org.deeplearning4j.scalphagozero.board.{ BlackPlayer, GoBoard, Player, WhitePlayer }

sealed trait VertexType extends Product with Serializable
case object BlackStone extends VertexType
case object WhiteStone extends VertexType
case object BlackTerritory extends VertexType
case object WhiteTerritory extends VertexType
case object Dame extends VertexType

/**
  * Compute the result of a game
  *
  * @author Max Pumperla
  */
final case class GameResult(
    numBlackStones: Int,
    numWhiteStones: Int,
    numBlackTerritory: Int,
    numWhiteTerritory: Int,
    numDame: Int,
    komi: Double
) {

  /**
    * Points black scored
    */
  val blackPoints: Int = numBlackTerritory + numBlackStones

  /**
    * points white scored
    */
  val whitePoints: Int = numWhiteTerritory + numWhiteStones

  val winner: Player = if (blackPoints > whitePoints + komi) BlackPlayer else WhitePlayer

  val winningMargin: Double = Math.abs(blackPoints - (whitePoints + komi))

  def toDebugString: String = {
    var s = s"blackTerritory ($numBlackTerritory) + blackStones ($numBlackStones) = $blackPoints\n"
    s += s"whiteTerritory ($numWhiteTerritory) + blackStones ($numWhiteStones) = $whitePoints\n"
    s += s"num dame = $numDame,  kome = $komi\n"
    s += toString
    s
  }

  override lazy val toString: String = {
    val white = whitePoints + komi
    winner match {
      case BlackPlayer => "B+ " + (blackPoints - white)
      case WhitePlayer => "W+ " + (white - blackPoints)
    }
  }
}

object GameResult {

  /**
    * Compute the game result from the current state.
    *
    * @param goBoard GoBoard instance
    * @return GameResult object
    */
  def computeGameResult(goBoard: GoBoard): GameResult = {
    val territoryCalculator = new TerritoryCalculator(goBoard)
    val territoryMap = territoryCalculator.evaluateTerritory()

    var numBlackStones = 0
    var numWhiteStones = 0
    var numBlackTerritory = 0
    var numWhiteTerritory = 0
    var numDame = 0

    for ((_, status) <- territoryMap) {
      status match {
        case BlackStone     => numBlackStones += 1
        case WhiteStone     => numWhiteStones += 1
        case BlackTerritory => numBlackTerritory += 1
        case WhiteTerritory => numWhiteTerritory += 1
        case Dame           => numDame += 1
      }
    }

    GameResult(
      numBlackStones = numBlackStones,
      numWhiteStones = numWhiteStones,
      numBlackTerritory = numBlackTerritory,
      numWhiteTerritory = numWhiteTerritory,
      numDame = numDame,
      komi = 7.5
    )
  }
}
