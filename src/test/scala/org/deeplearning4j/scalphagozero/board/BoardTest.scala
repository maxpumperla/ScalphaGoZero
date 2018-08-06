package org.deeplearning4j.scalphagozero.board

import org.scalatest.FunSpec

class BoardTest extends FunSpec {

  describe("A new 19x19 Board") {
    val board = GoBoard(19, 19)

    it("playing a black stone") {
      board.placeStone(Player(PlayerColor.black), Point(2, 2))
      board.placeStone(Player(PlayerColor.black), Point(1, 2))
      assert(board.getColor(Point(2, 2)).contains(PlayerColor.black))

      //      board.place_stone(Player.black, Point(2, 2))
//      board.place_stone(Player.white, Point(1, 2))
//      self.assertEqual(Player.black, board.get(Point(2, 2)))
//      board.place_stone(Player.white, Point(2, 1))
//      self.assertEqual(Player.black, board.get(Point(2, 2)))
//      board.place_stone(Player.white, Point(2, 3))
//      self.assertEqual(Player.black, board.get(Point(2, 2)))
//      board.place_stone(Player.white, Point(3, 2))
//      self.assertIsNone(board.get(Point(2, 2)))
    }
  }
}
