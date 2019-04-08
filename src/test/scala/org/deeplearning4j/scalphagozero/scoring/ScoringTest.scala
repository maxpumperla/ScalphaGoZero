package org.deeplearning4j.scalphagozero.scoring

import org.deeplearning4j.scalphagozero.board._
import org.scalatest.FunSpec

class ScoringTest extends FunSpec {

  // .w.ww
  // wwww.
  // bbbww
  // .bbbb
  // .b.b.
  describe("Creating a simple 5x5 game for scoring") {
    var board = GoBoard(5)

    board = board.placeStone(BlackPlayer, Point(1, 2))
    board = board.placeStone(BlackPlayer, Point(1, 4))
    board = board.placeStone(BlackPlayer, Point(2, 2))
    board = board.placeStone(BlackPlayer, Point(2, 3))
    board = board.placeStone(BlackPlayer, Point(2, 4))
    board = board.placeStone(BlackPlayer, Point(2, 5))
    board = board.placeStone(BlackPlayer, Point(3, 1))
    board = board.placeStone(BlackPlayer, Point(3, 2))
    board = board.placeStone(BlackPlayer, Point(3, 3))

    board = board.placeStone(WhitePlayer, Point(3, 4))
    board = board.placeStone(WhitePlayer, Point(3, 5))
    board = board.placeStone(WhitePlayer, Point(4, 1))
    board = board.placeStone(WhitePlayer, Point(4, 2))
    board = board.placeStone(WhitePlayer, Point(4, 3))
    board = board.placeStone(WhitePlayer, Point(4, 4))
    board = board.placeStone(WhitePlayer, Point(5, 2))
    board = board.placeStone(WhitePlayer, Point(5, 4))
    board = board.placeStone(WhitePlayer, Point(5, 5))
    //println("final board configuration: \n" + board)

    val result = GameResult.computeGameResult(board, 0.5f)
    //println("result = \n" + result.toDebugString)

    it("should have 9 black and white stones") {
      assert(9 == result.numBlackStones)
      assert(9 == result.numWhiteStones)
    }
    it("should have 4 points for black") {
      assert(4 == result.numBlackTerritory)
    }
    it("should have 3 points for white") {
      assert(3 == result.numWhiteTerritory)
    }
    it("and no dame points") {
      assert(0 == result.numDame)
    }
    it("Black wins by") {
      assert(result.blackWinningMargin == 0.5)
    }
  }

  describe("Creating a 5x5 game with captures for scoring") {
    var board = GoBoard(5)

    board = board.placeStone(BlackPlayer, Point(1, 4))
    board = board.placeStone(BlackPlayer, Point(2, 4))
    board = board.placeStone(BlackPlayer, Point(3, 4))
    board = board.placeStone(BlackPlayer, Point(4, 4))
    board = board.placeStone(BlackPlayer, Point(5, 4))

    board = board.placeStone(WhitePlayer, Point(1, 3))
    board = board.placeStone(WhitePlayer, Point(2, 3))
    board = board.placeStone(WhitePlayer, Point(3, 3))
    board = board.placeStone(WhitePlayer, Point(4, 3))
    board = board.placeStone(WhitePlayer, Point(5, 3))

    board = board.placeStone(BlackPlayer, Point(2, 2))
    board = board.placeStone(BlackPlayer, Point(3, 2))
    board = board.placeStone(BlackPlayer, Point(4, 2))

    board = board.placeStone(WhitePlayer, Point(2, 5))
    board = board.placeStone(WhitePlayer, Point(3, 5))
    board = board.placeStone(WhitePlayer, Point(4, 5))

    board = board.placeStone(BlackPlayer, Point(5, 5)) // fills second to last liberty
    board = board.placeStone(WhitePlayer, Point(1, 5)) // captures 6 black stones
    board = board.placeStone(BlackPlayer, Point(5, 2))
    board = board.placeStone(WhitePlayer, Point(4, 4)) // plays in white result
    board = board.placeStone(BlackPlayer, Point(1, 2)) // secures 5 points black result
    //println("final board configuration: \n" + board)

    val result = GameResult.computeGameResult(board, 0.5f)

    it("Expected stones on the board") {
      assert(result.numWhiteStones == 10)
      assert(result.numBlackStones == 5)

    }
    it("should have 4 points for black") {
      assert(5 == result.numBlackTerritory)
    }
    it("should have 3 points for white") {
      assert(5 == result.numWhiteTerritory)
    }
    it("and no dame points") {
      assert(0 == result.numDame)
    }
    it("White wins by") {
      assert(-result.blackWinningMargin == 11.5) // should be 7.5. Was 5.5
    }
  }

  describe("Creating a 5x5 game with captures and dame for scoring") {
    var board = GoBoard(5)

    board = board.placeStone(BlackPlayer, Point(1, 5))
    board = board.placeStone(BlackPlayer, Point(2, 5))
    board = board.placeStone(BlackPlayer, Point(3, 5))

    board = board.placeStone(WhitePlayer, Point(1, 4))
    board = board.placeStone(WhitePlayer, Point(2, 4))
    board = board.placeStone(WhitePlayer, Point(3, 4))
    board = board.placeStone(WhitePlayer, Point(4, 4))
    board = board.placeStone(WhitePlayer, Point(5, 4))

    board = board.placeStone(BlackPlayer, Point(1, 2))
    board = board.placeStone(BlackPlayer, Point(2, 2))
    board = board.placeStone(BlackPlayer, Point(3, 2))
    board = board.placeStone(BlackPlayer, Point(4, 2))
    board = board.placeStone(BlackPlayer, Point(5, 2))

    board = board.placeStone(WhitePlayer, Point(4, 5)) // capture 3 black stones and make life
    //println("final board configuration: \n" + board)

    val result = GameResult.computeGameResult(board, 0.5f)

    it("Expected stones on the board") {
      assert(result.numWhiteStones == 6)
      assert(result.numBlackStones == 5)
    }
    it("should have 4 points for black") {
      assert(5 == result.numBlackTerritory)
    }
    it("should have 3 points for white") {
      assert(4 == result.numWhiteTerritory)
    }
    it("and several dame points") {
      assert(5 == result.numDame)
    }
    it("White wins by") {
      assert(-result.blackWinningMargin == 3.5)
    }
  }

  describe("Creating a 5x5 game with big black victory") {
    var board = GoBoard(5)

    // first two rows of white stones that will be captured
    for (i <- 1 to 5) {
      board = board.placeStone(WhitePlayer, Point(1, i))
      board = board.placeStone(WhitePlayer, Point(2, i))
    }
    // now capture them
    for (i <- 1 to 5) {
      board = board.placeStone(BlackPlayer, Point(3, i))
    }
    // create white group with 2 eyes
    board = board.placeStone(WhitePlayer, Point(4, 1))
    board = board.placeStone(WhitePlayer, Point(4, 2))
    board = board.placeStone(WhitePlayer, Point(4, 3))
    board = board.placeStone(WhitePlayer, Point(4, 4))
    board = board.placeStone(WhitePlayer, Point(5, 2))
    board = board.placeStone(WhitePlayer, Point(5, 4))
    //println("final board configuration: \n" + board)

    val result = GameResult.computeGameResult(board, 0.5f)
    //println("result = \n" + result.toDebugString)

    it("Expected stones on the board") {
      assert(result.numWhiteStones == 6)
      assert(result.numBlackStones == 5)
    }
    it("should have 4 points for black") {
      assert(10 == result.numBlackTerritory)
    }
    it("should have 3 points for white") {
      assert(2 == result.numWhiteTerritory)
    }
    it("and dame points") {
      assert(2 == result.numDame)
    }
    it("Black wins by") {
      assert(result.blackWinningMargin == 16.5)
    }
  }
}
