package org.deeplearning4j.scalphagozero.scoring

import org.deeplearning4j.scalphagozero.board._
import org.scalatest.FunSpec

class ScoringTest extends FunSpec {

  // .w.ww
  // wwww.
  // bbbww
  // .bbbb
  // .b.b.
  describe("Creating an artificial 5x5 game for scoring, the territory") {
    val board = GoBoard(5, 5)

    board.placeStone(BlackPlayer, Point(1, 2))
    board.placeStone(BlackPlayer, Point(1, 4))
    board.placeStone(BlackPlayer, Point(2, 2))
    board.placeStone(BlackPlayer, Point(2, 3))
    board.placeStone(BlackPlayer, Point(2, 4))
    board.placeStone(BlackPlayer, Point(2, 5))
    board.placeStone(BlackPlayer, Point(3, 1))
    board.placeStone(BlackPlayer, Point(3, 2))
    board.placeStone(BlackPlayer, Point(3, 3))

    board.placeStone(WhitePlayer, Point(3, 4))
    board.placeStone(WhitePlayer, Point(3, 5))
    board.placeStone(WhitePlayer, Point(4, 1))
    board.placeStone(WhitePlayer, Point(4, 2))
    board.placeStone(WhitePlayer, Point(4, 3))
    board.placeStone(WhitePlayer, Point(4, 4))
    board.placeStone(WhitePlayer, Point(5, 2))
    board.placeStone(WhitePlayer, Point(5, 4))
    board.placeStone(WhitePlayer, Point(5, 5))

    val territory = GameResult.evaluateTerritory(board)

    it("should have 9 black and white stones") {
      assert(9 == territory.numBlackStones)
      assert(9 == territory.numWhiteStones)
    }
    it("should have 4 points for black") {
      //assert(4 == territory.numBlackTerritory)
    }
    it("should have 3 points for white") {
      assert(3 == territory.numWhiteTerritory)
    }
    it("and no dame points") {
      assert(0 == territory.numDame)
    }
  }

}
