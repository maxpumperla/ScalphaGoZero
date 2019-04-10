package org.deeplearning4j.scalphagozero.encoders

import org.deeplearning4j.scalphagozero.board._
import org.nd4j.linalg.api.ndarray.INDArray
import org.scalatest.FunSpec

class ZeroEncoderTest extends FunSpec {

  describe("Agent Encoding") {

    val encoder = new ZeroEncoder(5)

    it("encoding pass") {
      assert(encoder.encodeMove(Move.Pass) == 25)
    }

    it("encoding point move at 1,1") {
      assert(encoder.encodeMove(Move.Play(Point(1, 1))) == 0)
    }

    it("encoding point move at 2,3") {
      assert(encoder.encodeMove(Move.Play(Point(2, 3))) == 7)
    }

    it("encoding point move at 3,2") {
      assert(encoder.encodeMove(Move.Play(Point(3, 2))) == 11)
    }

    it("encoding point move at 3,3") {
      assert(encoder.encodeMove(Move.Play(Point(3, 3))) == 12)
    }
    it("encoding point move at 5,5") {
      assert(encoder.encodeMove(Move.Play(Point(5, 5))) == 24)
    }

    it("cannot encode resignation") {
      assertThrows[IllegalArgumentException] {
        encoder.encodeMove(Move.Resign)
      }
    }
  }

  describe("Agent Decoding") {

    val encoder = new ZeroEncoder(5)

    it("decoding pass") {
      assert(encoder.decodeMoveIndex(25) == Move.Pass)
    }

    it("encoding point move at 1,1") {
      assert(encoder.decodeMoveIndex(0) == Move.Play(Point(1, 1)))
    }

    it("encoding point move at 2,3") {
      assert(encoder.decodeMoveIndex(7) == Move.Play(Point(2, 3)))
      assert(encoder.encodeMove(Move.Play(Point(2, 3))) == 7)
    }
  }

  describe("Simple 5x5 Board encoding") {
    val encoder = new ZeroEncoder(5)

    val board = createSimple5x5GoBoard()
    val gameState = new GameState(board, BlackPlayer)

    val a: INDArray = encoder.encode(gameState)

    it("encoded shape is") {
      assertResult("1, 11, 5, 5") { a.shape().mkString(", ") }
    }

    // 5 .O.O.
    // 4 OOOO.
    // 3 .XX.X
    // 2 .X.X.
    // 1 .XOX.
    //   ABCDE
    it("encodes as") {
      val expLayers = Seq(
        """0, 0, 0, 0, 0
        |0, 0, 0, 0, 0
        |0, 0, 0, 0, 0
        |0, 0, 0, 0, 0
        |0, 0, 1, 0, 0""",
        """0, 0, 0, 0, 0
        |0, 0, 0, 0, 0
        |0, 0, 0, 0, 0
        |0, 0, 0, 0, 0
        |0, 0, 0, 0, 0""",
        """0, 0, 0, 0, 0
        |0, 0, 0, 0, 0
        |0, 0, 0, 0, 0
        |0, 0, 0, 0, 0
        |0, 0, 0, 0, 0""",
        """0, 1, 0, 1, 0
        |1, 1, 1, 1, 0
        |0, 0, 0, 0, 0
        |0, 0, 0, 0, 0
        |0, 0, 0, 0, 0""",
        """0, 0, 0, 0, 0
        |0, 0, 0, 0, 0
        |0, 0, 0, 0, 0
        |0, 0, 0, 0, 0
        |0, 0, 0, 0, 0""",
        """0, 0, 0, 0, 0
        |0, 0, 0, 0, 0
        |0, 0, 0, 0, 0
        |0, 0, 0, 0, 0
        |0, 0, 0, 0, 0""",
        """0, 0, 0, 0, 0
        |0, 0, 0, 0, 0
        |0, 0, 0, 0, 1
        |0, 0, 0, 0, 0
        |0, 0, 0, 0, 0""",
        """0, 0, 0, 0, 0
        |0, 0, 0, 0, 0
        |0, 1, 1, 0, 0
        |0, 1, 0, 1, 0
        |0, 1, 0, 1, 0""",
        """0, 0, 0, 0, 0
        |0, 0, 0, 0, 0
        |0, 0, 0, 0, 0
        |0, 0, 0, 0, 0
        |0, 0, 0, 0, 0""",
        """1, 1, 1, 1, 1
        |1, 1, 1, 1, 1
        |1, 1, 1, 1, 1
        |1, 1, 1, 1, 1
        |1, 1, 1, 1, 1""",
        """0, 0, 0, 0, 0
        |0, 0, 0, 0, 0
        |0, 0, 0, 0, 0
        |0, 0, 0, 0, 0
        |0, 0, 0, 0, 0"""
      ).map(s => s.stripMargin)

      val b = a.slice(0)

      for (i <- 1 to 11) {
        println("layer " + i)
        //println(b.slice(i).toIntMatrix.map(_.mkString(", ")).mkString("\n"))
        assertResult(expLayers(i - 1)) {
          b.slice(i - 1).toIntMatrix.map(_.mkString(", ")).mkString("\n")
        }
      }
    }
  }

  // I don't think that this tets currently gives a correct result for the ko layer
  describe("5x5 Board encoding (with ko)") {
    val encoder = new ZeroEncoder(5)

    val board = create5x5GoBoardWithKo()
    val gameState = new GameState(board, BlackPlayer)

    val a: INDArray = encoder.encode(gameState)

    it("encoded shape is") {
      assertResult("1, 11, 5, 5") { a.shape().mkString(", ") }
    }

    // 5 ....O
    // 4 .O.XO
    // 3 .XXO.
    // 2 .X.XO
    // 1 .....
    //   ABCDE
    it("encodes as") {
      val expLayers = Seq(
        """0, 0, 0, 0, 0
          |0, 0, 0, 0, 0
          |0, 0, 0, 1, 0
          |0, 0, 0, 0, 0
          |0, 0, 0, 0, 0""",
        """0, 0, 0, 0, 1
          |0, 0, 0, 0, 1
          |0, 0, 0, 0, 0
          |0, 0, 0, 0, 1
          |0, 0, 0, 0, 0""",
        """0, 0, 0, 0, 0
          |0, 1, 0, 0, 0
          |0, 0, 0, 0, 0
          |0, 0, 0, 0, 0
          |0, 0, 0, 0, 0""",
        """0, 0, 0, 0, 0
          |0, 0, 0, 0, 0
          |0, 0, 0, 0, 0
          |0, 0, 0, 0, 0
          |0, 0, 0, 0, 0""",
        """0, 0, 0, 0, 0
          |0, 0, 0, 0, 0
          |0, 0, 0, 0, 0
          |0, 0, 0, 0, 0
          |0, 0, 0, 0, 0""",
        """0, 0, 0, 0, 0
          |0, 0, 0, 1, 0
          |0, 0, 0, 0, 0
          |0, 0, 0, 1, 0
          |0, 0, 0, 0, 0""",
        """0, 0, 0, 0, 0
          |0, 0, 0, 0, 0
          |0, 0, 0, 0, 0
          |0, 0, 0, 0, 0
          |0, 0, 0, 0, 0""",
        """0, 0, 0, 0, 0
          |0, 0, 0, 0, 0
          |0, 1, 1, 0, 0
          |0, 1, 0, 0, 0
          |0, 0, 0, 0, 0""",
        """0, 0, 0, 0, 0
          |0, 0, 0, 0, 0
          |0, 0, 0, 0, 0
          |0, 0, 0, 0, 0
          |0, 0, 0, 0, 0""",
        """1, 1, 1, 1, 1
          |1, 1, 1, 1, 1
          |1, 1, 1, 1, 1
          |1, 1, 1, 1, 1
          |1, 1, 1, 1, 1""",
        """0, 0, 0, 0, 0
          |0, 0, 0, 0, 0
          |0, 0, 0, 0, 0
          |0, 0, 0, 0, 0
          |0, 0, 0, 0, 0"""
      ).map(s => s.stripMargin)

      val b = a.slice(0)

      for (i <- 1 to 11) {
        println("layer " + i)
        //println(b.slice(i - 1).toIntMatrix.map(_.mkString(", ")).mkString("\n"))
        assertResult(expLayers(i - 1)) {
          b.slice(i - 1).toIntMatrix.map(_.mkString(", ")).mkString("\n")
        }
      }
    }
  }

  private def createSimple5x5GoBoard(): GoBoard = {
    var board = GoBoard(5)
    board = board.placeStone(BlackPlayer, Point(3, 3))
    board = board.placeStone(WhitePlayer, Point(2, 3))
    board = board.placeStone(BlackPlayer, Point(3, 2))
    board = board.placeStone(WhitePlayer, Point(2, 2))
    board = board.placeStone(BlackPlayer, Point(4, 2))
    board = board.placeStone(WhitePlayer, Point(2, 1))
    board = board.placeStone(BlackPlayer, Point(4, 4))
    board = board.placeStone(WhitePlayer, Point(5, 3))
    board = board.placeStone(BlackPlayer, Point(5, 2))
    board = board.placeStone(WhitePlayer, Point(2, 4))
    board = board.placeStone(BlackPlayer, Point(3, 5))
    board = board.placeStone(WhitePlayer, Point(1, 4))
    board = board.placeStone(BlackPlayer, Point(5, 4))
    board = board.placeStone(WhitePlayer, Point(1, 2))
    println(board)

    board
  }

  private def create5x5GoBoardWithKo(): GoBoard = {
    var board = GoBoard(5)
    board = board.placeStone(BlackPlayer, Point(3, 3))
    board = board.placeStone(WhitePlayer, Point(3, 4))
    board = board.placeStone(BlackPlayer, Point(2, 4))
    board = board.placeStone(WhitePlayer, Point(2, 5))
    board = board.placeStone(BlackPlayer, Point(4, 4))
    board = board.placeStone(WhitePlayer, Point(1, 5))
    board = board.placeStone(BlackPlayer, Point(4, 2))
    board = board.placeStone(WhitePlayer, Point(4, 5))
    board = board.placeStone(BlackPlayer, Point(3, 5)) // take the ko
    board = board.placeStone(WhitePlayer, Point(2, 2)) // white threat
    board = board.placeStone(BlackPlayer, Point(3, 2)) // black responds
    board = board.placeStone(WhitePlayer, Point(3, 4)) // white retakes the ko
    println(board)

    board
  }
}
