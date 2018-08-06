package org.deeplearning4j.scalphagozero.board

import org.scalatest.FunSpec

class BoardTest extends FunSpec {

  describe("Capturing a stone on a new 19x19 Board") {
    val board = GoBoard(19, 19)

    it("should place and confirm a black stone") {
      board.placeStone(Player(PlayerColor.black), Point(2, 2))
      board.placeStone(Player(PlayerColor.white), Point(1, 2))
      assert(board.getColor(Point(2, 2)).contains(PlayerColor.black))
    }
    it("if black's liberties go down to two, the stone should still be there") {
      board.placeStone(Player(PlayerColor.white), Point(2, 1))
      assert(board.getColor(Point(2, 2)).contains(PlayerColor.black))
    }
    it("if black's liberties go down to one, the stone should still be there") {
      board.placeStone(Player(PlayerColor.white), Point(2, 3))
      assert(board.getColor(Point(2, 2)).contains(PlayerColor.black))
    }
    it("finally, if all liberties are taken, the stone should be gone") {
      board.placeStone(Player(PlayerColor.white), Point(3, 2))
      assert(board.getColor(Point(2, 2)).isEmpty)
    }
  }

  describe("Capturing two stones on a new 19x19 Board") {
    val board = GoBoard(19, 19)

    it("should place and confirm two black stones") {
      board.placeStone(Player(PlayerColor.black), Point(2, 2))
      board.placeStone(Player(PlayerColor.black), Point(2, 3))
      board.placeStone(Player(PlayerColor.white), Point(1, 2))
      board.placeStone(Player(PlayerColor.white), Point(1, 3))

      assert(board.getColor(Point(2, 2)).contains(PlayerColor.black))
      assert(board.getColor(Point(2, 3)).contains(PlayerColor.black))
    }
    it("if black's liberties go down to two, the stone should still be there") {
      board.placeStone(Player(PlayerColor.white), Point(3, 2))
      board.placeStone(Player(PlayerColor.white), Point(3, 3))
      assert(board.getColor(Point(2, 2)).contains(PlayerColor.black))
      assert(board.getColor(Point(2, 3)).contains(PlayerColor.black))

    }
//    it("finally, if all liberties are taken, the stone should be gone") {
//      board.placeStone(Player(PlayerColor.white), Point(2, 1))
//      board.placeStone(Player(PlayerColor.white), Point(2, 4))
//      assert(board.getColor(Point(2, 2)).isEmpty)
//    }
  }

  describe("If you capture a stone, it's not suicide") {
    val board = GoBoard(19, 19)
    it("should regain liberties by capturing") {
      board.placeStone(Player(PlayerColor.black), Point(1, 1))
      board.placeStone(Player(PlayerColor.black), Point(2, 2))
      board.placeStone(Player(PlayerColor.black), Point(1, 3))
      board.placeStone(Player(PlayerColor.white), Point(2, 1))
      board.placeStone(Player(PlayerColor.white), Point(1, 2))
      assert(board.getColor(Point(1, 1)).isEmpty)
      assert(board.getColor(Point(2, 1)).contains(PlayerColor.white))
      assert(board.getColor(Point(1, 2)).contains(PlayerColor.white))
    }
  }
}
