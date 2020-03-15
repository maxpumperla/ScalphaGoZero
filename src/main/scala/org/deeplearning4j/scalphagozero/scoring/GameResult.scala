package org.deeplearning4j.scalphagozero.scoring

import org.deeplearning4j.scalphagozero.board.{ BlackPlayer, GoBoard, Player, WhitePlayer }

/**
  * Compute the result of a game
  * numBlackCaptures refers to the number of white stones captured by black.
  * If a player won by resignation, they are the winner regardless of the score statistics.
  * For the scoring calculation to be most accurate, the game has to really be over in the sense that
  * all stones that can be captured are captured. There is no cost to filling in your own territory as
  * long as you do not fill in either of your last 2 eyes.
  * @author Max Pumperla
  * @author Barry Becker
  */
final case class GameResult(
    numBlackStones: Int,
    numWhiteStones: Int,
    numBlackCaptures: Int,
    numWhiteCaptures: Int,
    numBlackTerritory: Int,
    numWhiteTerritory: Int,
    numDame: Int,
    komi: Float,
    wonByResignation: Option[Player]
) {

  /**
    * Points black scored
    */
  val blackPoints: Int = numBlackTerritory + numBlackStones + numBlackCaptures

  /**
    * points white scored
    */
  val whitePoints: Int = numWhiteTerritory + numWhiteStones + numWhiteCaptures

  val blackWinningMargin: Float = blackPoints - (whitePoints + komi)
  val winner: Player = if (blackWinningMargin > 0) BlackPlayer else WhitePlayer

  def toDebugString: String = {
    var s =
      s"Black: territory($numBlackTerritory) + stones($numBlackStones) + captures($numBlackCaptures) = $blackPoints\n"
    s += s"White: territory($numWhiteTerritory) + stones($numWhiteStones) + captures($numWhiteCaptures) = $whitePoints\n"
    s += s"num dame = $numDame,  komi = $komi\n"
    s += toString
    s
  }

  override lazy val toString: String = {
    if (wonByResignation.isDefined) {
      val winningPlayer = if (wonByResignation.get == BlackPlayer) "Black" else "White"
      winningPlayer + " won by resignation"
    } else {
      winner match {
        case BlackPlayer => "Black +" + blackWinningMargin
        case WhitePlayer => "White +" + -blackWinningMargin
      }
    }
  }
}

object GameResult {

  val DEFAULT_KOMI = 6.5f

  /**
    * Compute the game result from the current state.
    *
    * @param goBoard GoBoard instance
    * @return GameResult object
    */
  def apply(goBoard: GoBoard, komi: Float = DEFAULT_KOMI, wonByResignation: Option[Player] = None): GameResult = {
    val territoryCalculator = new TerritoryCalculator(goBoard)
    val territoryMap = territoryCalculator.evaluateTerritory()

    var numBlackStones = 0
    var numWhiteStones = 0
    var numBlackTerritory = 0
    var numWhiteTerritory = 0
    var numBlackCaptures = 0
    var numWhiteCaptures = 0
    var numDame = 0

    for ((_, status) <- territoryMap) {
      status match {
        case BlackStone     => numBlackStones += 1
        case WhiteStone     => numWhiteStones += 1
        case BlackTerritory => numBlackTerritory += 1
        case WhiteTerritory => numWhiteTerritory += 1
        case CapturedBlackStone =>
          numWhiteTerritory += 1
          numWhiteCaptures += 1
        case CapturedWhiteStone =>
          numBlackTerritory += 1
          numBlackCaptures += 1
        case Dame => numDame += 1
      }
    }

    GameResult(
      numBlackStones,
      numWhiteStones,
      goBoard.blackCaptures + numBlackCaptures,
      goBoard.whiteCaptures + numWhiteCaptures,
      numBlackTerritory,
      numWhiteTerritory,
      numDame,
      komi,
      wonByResignation
    )
  }
}
