package org.deeplearning4j.scalphagozero.board

import org.scalatest.FunSpec

class BoardTest extends FunSpec {

  describe("Capturing a stone on a new 19x19 Board") {
    var board = GoBoard(9)

    it("should place and confirm a black stone") {
      board = board.placeStone(BlackPlayer, Point(2, 2))
      board = board.placeStone(WhitePlayer, Point(1, 2))
      assert(board.getPlayer(Point(2, 2)).contains(BlackPlayer))
    }
    it("if black's liberties go down to two, the stone should still be there") {
      board = board.placeStone(WhitePlayer, Point(2, 1))
      assert(board.getPlayer(Point(2, 2)).contains(BlackPlayer))
    }
    it("if black's liberties go down to one, the stone should still be there") {
      board = board.placeStone(WhitePlayer, Point(2, 3))
      assert(board.getPlayer(Point(2, 2)).contains(BlackPlayer))
    }
    it("finally, if all liberties are taken, the stone should be gone") {
      board = board.placeStone(WhitePlayer, Point(3, 2))
      assert(board.getPlayer(Point(2, 2)).isEmpty)
    }
    println(board)
  }

  describe("Capturing two stones on a new 19x19 Board") {
    var board = GoBoard(9)

    it("should place and confirm two black stones") {
      board = board.placeStone(BlackPlayer, Point(2, 2))
      board = board.placeStone(BlackPlayer, Point(2, 3))
      board = board.placeStone(WhitePlayer, Point(1, 2))
      board = board.placeStone(WhitePlayer, Point(1, 3))

      assert(board.getPlayer(Point(2, 2)).contains(BlackPlayer))
      assert(board.getPlayer(Point(2, 3)).contains(BlackPlayer))
      println(board)
    }
    it("if black's liberties go down to two, the stone should still be there") {
      board = board.placeStone(WhitePlayer, Point(3, 2))
      board = board.placeStone(WhitePlayer, Point(3, 3))
      assert(board.getPlayer(Point(2, 2)).contains(BlackPlayer))
      assert(board.getPlayer(Point(2, 3)).contains(BlackPlayer))
      println(board)
    }
    it("finally, if all liberties are taken, the stone should be gone") {
      board = board.placeStone(WhitePlayer, Point(2, 1))
      board = board.placeStone(WhitePlayer, Point(2, 4))
      println(board)
      assert(board.getPlayer(Point(2, 2)).isEmpty)
      assert(board.getPlayer(Point(2, 3)).isEmpty)
    }
  }

  describe("If you capture a stone, it's not suicide") {
    var board = GoBoard(9)
    it("should regain liberties by capturing") {
      board = board.placeStone(BlackPlayer, Point(1, 1))
      board = board.placeStone(BlackPlayer, Point(2, 2))
      board = board.placeStone(BlackPlayer, Point(1, 3))
      board = board.placeStone(WhitePlayer, Point(2, 1))
      board = board.placeStone(WhitePlayer, Point(1, 2))
      assert(board.getPlayer(Point(1, 1)).isEmpty)
      assert(board.getPlayer(Point(2, 1)).contains(WhitePlayer))
      assert(board.getPlayer(Point(1, 2)).contains(WhitePlayer))
    }
    println(board)
  }

  describe("Filling your own eye is prohibited") {
    var board = createBoardWithEyes()
    it("Filling non-eye allowed") {
      assert(!board.doesMoveFillEye(WhitePlayer, Point(2, 1)))
      assert(!board.doesMoveFillEye(WhitePlayer, Point(1, 1)))
      assert(!board.doesMoveFillEye(WhitePlayer, Point(1, 5)))
      assert(!board.doesMoveFillEye(WhitePlayer, Point(2, 4)))
      assert(!board.doesMoveFillEye(BlackPlayer, Point(2, 1)))
      assert(!board.doesMoveFillEye(WhitePlayer, Point(5, 5)))
      assert(!board.doesMoveFillEye(BlackPlayer, Point(5, 1)))
    }
    it("Filling eye not allowed") {
      assert(board.doesMoveFillEye(WhitePlayer, Point(5, 1)))
      assert(board.doesMoveFillEye(WhitePlayer, Point(3, 3)))
    }
    println(board)
  }

  /**
    * 1 .....
    * 2 .oo.o
    * 3 xo.o.
    * 4 ooooo
    * 5 .o...
    */
  def createBoardWithEyes(): GoBoard = {
    var board = GoBoard(5)
    board = board.placeStone(WhitePlayer, Point(4, 1))
    board = board.placeStone(WhitePlayer, Point(4, 2))
    board = board.placeStone(WhitePlayer, Point(4, 3))
    board = board.placeStone(WhitePlayer, Point(4, 4))
    board = board.placeStone(WhitePlayer, Point(4, 5))
    board = board.placeStone(WhitePlayer, Point(5, 2))
    board = board.placeStone(WhitePlayer, Point(3, 2))
    board = board.placeStone(WhitePlayer, Point(3, 4))
    board = board.placeStone(WhitePlayer, Point(2, 2))
    board = board.placeStone(WhitePlayer, Point(2, 3))
    board = board.placeStone(WhitePlayer, Point(2, 5))
    board = board.placeStone(BlackPlayer, Point(3, 1))
    board
  }

  describe("Test removing liberties:") {
    it("a stone with four liberties should end up with three if an opponent stone is added") {
      var board = GoBoard(5)
      board = board.placeStone(BlackPlayer, Point(3, 3))
      board = board.placeStone(WhitePlayer, Point(2, 2))
      val whiteString = board.getGoString(Point(2, 2)).get
      assert(whiteString.numLiberties == 4)

      board = board.placeStone(BlackPlayer, Point(3, 2))
      //val newWhiteString = board.getGoString(Point(2, 2)).get
      //assert(whiteString.numLiberties == 3)
    }
  }

  describe("Empty triangle test:") {
    it("an empty triangle in the corner with one white stone should have 3 liberties") {
      // x x
      // x o
      var board = GoBoard(5)
      board = board.placeStone(BlackPlayer, Point(1, 1))
      board = board.placeStone(BlackPlayer, Point(1, 2))
      board = board.placeStone(BlackPlayer, Point(2, 2))
      board = board.placeStone(WhitePlayer, Point(2, 1))

      val blackString: GoString = board.getGoString(Point(1, 1)).get

      assert(blackString.numLiberties == 3)
      assert(blackString.liberties.contains(Point(3, 2)))
      assert(blackString.liberties.contains(Point(2, 3)))
      assert(blackString.liberties.contains(Point(1, 3)))
      println(board)
    }
  }

  describe("Test self capture:") {
    // ooo..
    // x.xo.
    it("black can't take it's own last liberty") {
      var board = GoBoard(5)
      board = board.placeStone(BlackPlayer, Point(1, 1))
      board = board.placeStone(BlackPlayer, Point(1, 3))
      board = board.placeStone(WhitePlayer, Point(2, 1))
      board = board.placeStone(WhitePlayer, Point(2, 2))
      board = board.placeStone(WhitePlayer, Point(2, 3))
      board = board.placeStone(WhitePlayer, Point(1, 4))
      println(board)
      assert(board.isSelfCapture(BlackPlayer, Point(1, 2)))
    }

    // o.o..
    // x.xo.
    it("but if we remove one white stone, the move becomes legal") {
      var board = GoBoard(5)
      board = board.placeStone(BlackPlayer, Point(1, 1))
      board = board.placeStone(BlackPlayer, Point(1, 3))
      board = board.placeStone(WhitePlayer, Point(2, 1))
      board = board.placeStone(WhitePlayer, Point(2, 3))
      board = board.placeStone(WhitePlayer, Point(1, 4))
      println(board)
      assert(!board.isSelfCapture(BlackPlayer, Point(1, 2)))
    }

    // xx...
    // oox..
    // x.o..
    it("if we capture a stone in the process, it's not self-atari") {
      var board = GoBoard(5)
      board = board.placeStone(BlackPlayer, Point(3, 1))
      board = board.placeStone(BlackPlayer, Point(3, 2))
      board = board.placeStone(BlackPlayer, Point(2, 3))
      board = board.placeStone(BlackPlayer, Point(1, 1))
      board = board.placeStone(WhitePlayer, Point(2, 1))
      board = board.placeStone(WhitePlayer, Point(2, 2))
      board = board.placeStone(WhitePlayer, Point(1, 3))
      println(board)
      assert(!board.isSelfCapture(BlackPlayer, Point(1, 2)))
    }

    // xx...
    // o.x..
    // xxx..
    it("Should not be able refill eye after capture") {
      var board = GoBoard(5)
      board = board.placeStone(BlackPlayer, Point(3, 1))
      board = board.placeStone(BlackPlayer, Point(3, 2))
      board = board.placeStone(BlackPlayer, Point(2, 3))
      board = board.placeStone(BlackPlayer, Point(1, 1))
      board = board.placeStone(WhitePlayer, Point(2, 1))
      board = board.placeStone(WhitePlayer, Point(2, 2))
      board = board.placeStone(BlackPlayer, Point(1, 3))
      println(board)
      board = board.placeStone(BlackPlayer, Point(1, 2)) // captures 2 stones
      assert(board.getPlayer(Point(1, 2)).contains(BlackPlayer))
      println("just played Black at 1, 2 (capturing 2 white stones)\n" + board)
      board = board.placeStone(WhitePlayer, Point(2, 1)) // refill first of 2 spaces in eye
      println("just played White at 2, 1\n" + board)
      assert(board.getPlayer(Point(1, 2)).contains(BlackPlayer))

      assert(board.isSelfCapture(WhitePlayer, Point(2, 2)))
    }

    // xx...
    // o.x..
    // xxo..
    it("OK to refill eye after capture if doing so captures opponent stones") {
      var board = GoBoard(5)
      board = board.placeStone(BlackPlayer, Point(3, 1))
      board = board.placeStone(BlackPlayer, Point(3, 2))
      board = board.placeStone(BlackPlayer, Point(2, 3))
      board = board.placeStone(BlackPlayer, Point(1, 1))
      board = board.placeStone(WhitePlayer, Point(2, 1))
      board = board.placeStone(WhitePlayer, Point(2, 2))
      board = board.placeStone(WhitePlayer, Point(1, 3))
      println(board)
      board = board.placeStone(BlackPlayer, Point(1, 2)) // captures 2 stones
      assert(board.getPlayer(Point(1, 2)).contains(BlackPlayer))
      println("just played Black at 1, 2 (capturing 2 white stones)\n" + board)
      board = board.placeStone(WhitePlayer, Point(2, 1)) // refill first of 2 spaces in eye
      println("just played White at 2, 1\n" + board)
      assert(board.getPlayer(Point(1, 2)).contains(BlackPlayer))

      println("White playing at 2,2 is OK because it captures 2 plack stones in doing so")
      assert(!board.isSelfCapture(WhitePlayer, Point(2, 2)))
      board = board.placeStone(WhitePlayer, Point(2, 2))
      println("just played White at 2, 2\n" + board)
    }
  }
}
