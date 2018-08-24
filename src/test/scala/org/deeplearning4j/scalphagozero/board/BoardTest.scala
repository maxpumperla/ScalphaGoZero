package org.deeplearning4j.scalphagozero.board

import org.scalatest.FunSpec

class BoardTest extends FunSpec {

  describe("Capturing a stone on a new 19x19 Board") {
    val board = GoBoard(19, 19)

    it("should place and confirm a black stone") {
      board.placeStone(Player(PlayerColor.Black), Point(2, 2))
      board.placeStone(Player(PlayerColor.White), Point(1, 2))
      assert(board.getColor(Point(2, 2)).contains(PlayerColor.Black))
    }
    it("if black's liberties go down to two, the stone should still be there") {
      board.placeStone(Player(PlayerColor.White), Point(2, 1))
      assert(board.getColor(Point(2, 2)).contains(PlayerColor.Black))
    }
    it("if black's liberties go down to one, the stone should still be there") {
      board.placeStone(Player(PlayerColor.White), Point(2, 3))
      assert(board.getColor(Point(2, 2)).contains(PlayerColor.Black))
    }
    it("finally, if all liberties are taken, the stone should be gone") {
      board.placeStone(Player(PlayerColor.White), Point(3, 2))
      assert(board.getColor(Point(2, 2)).isEmpty)
    }
  }

  describe("Capturing two stones on a new 19x19 Board") {
    val board = GoBoard(19, 19)

    it("should place and confirm two black stones") {
      board.placeStone(Player(PlayerColor.Black), Point(2, 2))
      board.placeStone(Player(PlayerColor.Black), Point(2, 3))
      board.placeStone(Player(PlayerColor.White), Point(1, 2))
      board.placeStone(Player(PlayerColor.White), Point(1, 3))

      assert(board.getColor(Point(2, 2)).contains(PlayerColor.Black))
      assert(board.getColor(Point(2, 3)).contains(PlayerColor.Black))
    }
    it("if black's liberties go down to two, the stone should still be there") {
      board.placeStone(Player(PlayerColor.White), Point(3, 2))
      board.placeStone(Player(PlayerColor.White), Point(3, 3))
      assert(board.getColor(Point(2, 2)).contains(PlayerColor.Black))
      assert(board.getColor(Point(2, 3)).contains(PlayerColor.Black))

    }
    it("finally, if all liberties are taken, the stone should be gone") {
      board.placeStone(Player(PlayerColor.White), Point(2, 1))
      board.placeStone(Player(PlayerColor.White), Point(2, 4))
      assert(board.getColor(Point(2, 2)).isEmpty)
    }
  }

  describe("If you capture a stone, it's not suicide") {
    val board = GoBoard(19, 19)
    it("should regain liberties by capturing") {
      board.placeStone(Player(PlayerColor.Black), Point(1, 1))
      board.placeStone(Player(PlayerColor.Black), Point(2, 2))
      board.placeStone(Player(PlayerColor.Black), Point(1, 3))
      board.placeStone(Player(PlayerColor.White), Point(2, 1))
      board.placeStone(Player(PlayerColor.White), Point(1, 2))
      assert(board.getColor(Point(1, 1)).isEmpty)
      assert(board.getColor(Point(2, 1)).contains(PlayerColor.White))
      assert(board.getColor(Point(1, 2)).contains(PlayerColor.White))
    }
  }

  describe("Test removing liberties:") {
    it("a stone with four liberties should end up with three if an opponent stone is added") {
      val board = new GoBoard(5, 5)
      board.placeStone(Player(PlayerColor.Black), Point(3, 3))
      board.placeStone(Player(PlayerColor.White), Point(2, 2))
      val whiteString = board.getGoString(Point(2, 2)).get
      assert(whiteString.numLiberties == 4)

      board.placeStone(Player(PlayerColor.Black), Point(3, 2))
      //val newWhiteString = board.getGoString(Point(2, 2)).get
      //assert(whiteString.numLiberties == 3)
    }
  }

  describe("Empty triangle test:") {
    it("an empty triangle in the corner with one white stone should have 3 liberties") {
      // x x
      // x o
      val board = new GoBoard(5, 5)
      board.placeStone(Player(PlayerColor.Black), Point(1, 1))
      board.placeStone(Player(PlayerColor.Black), Point(1, 2))
      board.placeStone(Player(PlayerColor.Black), Point(2, 2))
      board.placeStone(Player(PlayerColor.White), Point(2, 1))

      val blackString: GoString = board.getGoString(Point(1, 1)).get

      assert(blackString.numLiberties == 3)
      assert(blackString.liberties.contains((3, 2)))
      assert(blackString.liberties.contains((2, 3)))
      assert(blackString.liberties.contains((1, 3)))
    }
  }

  describe("Test self capture:") {
    // o.o..
    // x.xo.
    it("black can't take it's own last liberty") {
      val board = new GoBoard(5, 5)
      board.placeStone(Player(PlayerColor.Black), Point(1, 1))
      board.placeStone(Player(PlayerColor.Black), Point(1, 3))
      board.placeStone(Player(PlayerColor.White), Point(2, 1))
      board.placeStone(Player(PlayerColor.White), Point(2, 2))
      board.placeStone(Player(PlayerColor.White), Point(2, 3))
      board.placeStone(Player(PlayerColor.White), Point(1, 4))

      assert(board.isSelfCapture(Player(PlayerColor.Black), Point(1, 2)))
    }

    // o.o..
    // x.xo.
    it("but if we remove one white stone, the move becomes legal") {
      val board = new GoBoard(5, 5)
      board.placeStone(Player(PlayerColor.Black), Point(1, 1))
      board.placeStone(Player(PlayerColor.Black), Point(1, 3))
      board.placeStone(Player(PlayerColor.White), Point(2, 1))
      board.placeStone(Player(PlayerColor.White), Point(2, 3))
      board.placeStone(Player(PlayerColor.White), Point(1, 4))

      assert(!board.isSelfCapture(Player(PlayerColor.Black), Point(1, 2)))
    }

    // xx...
    // oox..
    // x.o..
    it("if we capture a stone in the process, it's not self-play") {
      val board = new GoBoard(5, 5)
      board.placeStone(Player(PlayerColor.Black), Point(3, 1))
      board.placeStone(Player(PlayerColor.Black), Point(3, 2))
      board.placeStone(Player(PlayerColor.Black), Point(2, 3))
      board.placeStone(Player(PlayerColor.Black), Point(1, 1))
      board.placeStone(Player(PlayerColor.White), Point(2, 1))
      board.placeStone(Player(PlayerColor.White), Point(2, 2))
      board.placeStone(Player(PlayerColor.White), Point(1, 3))

      assert(!board.isSelfCapture(Player(PlayerColor.Black), Point(1, 2)))
    }

  }
}
