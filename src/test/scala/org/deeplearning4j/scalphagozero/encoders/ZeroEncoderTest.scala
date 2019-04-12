package org.deeplearning4j.scalphagozero.encoders

import org.deeplearning4j.scalphagozero.board.Move.Play
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
      assert(encoder.encodeMove(Play(Point(1, 1))) == 0)
    }

    it("encoding point move at 2,3") {
      assert(encoder.encodeMove(Play(2, 3)) == 7)
    }

    it("encoding point move at 3,2") {
      assert(encoder.encodeMove(Play(3, 2)) == 11)
    }

    it("encoding point move at 3,3") {
      assert(encoder.encodeMove(Play(3, 3)) == 12)
    }
    it("encoding point move at 5,5") {
      assert(encoder.encodeMove(Play(5, 5)) == 24)
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
      assert(encoder.decodeMoveIndex(0) == Play(1, 1))
    }

    it("encoding point move at 2,3") {
      assert(encoder.decodeMoveIndex(7) == Play(2, 3))
      assert(encoder.encodeMove(Play(2, 3)) == 7)
    }
  }

  describe("Simple 5x5 Board encoding") {
    val encoder = new ZeroEncoder(5)
    val gameState = createSimple5x5GameState()
    val a: INDArray = encoder.encode(gameState)

    it("encoded shape is") {
      assertResult("1, 11, 5, 5") { a.shape().mkString(", ") }
    }

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
        //println("layer " + i)
        //println(b.slice(i).toIntMatrix.map(_.mkString(", ")).mkString("\n"))
        assertResult(expLayers(i - 1)) {
          b.slice(i - 1).toIntMatrix.map(_.mkString(", ")).mkString("\n")
        }
      }
    }
  }

  describe("5x5 Board encoding (with ko)") {
    val encoder = new ZeroEncoder(5)
    val gameState = create5x5GameStateWithKo()
    val a: INDArray = encoder.encode(gameState)

    it("encoded shape is") {
      assertResult("1, 11, 5, 5") { a.shape().mkString(", ") }
    }

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
          |0, 0, 0, 0, 1
          |0, 0, 0, 0, 0
          |0, 0, 0, 0, 0""" // there is one here somewhere to indicate the ko
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

  // 5 .O.O.
  // 4 OOOO.
  // 3 .XX.X
  // 2 .X.X.
  // 1 .XOX.
  //   ABCDE
  private def createSimple5x5GameState(): GameState = {
    var state = GameState(GoBoard(5), BlackPlayer)
    state = state.applyMove(Play(3, 3))
    state = state.applyMove(Play(2, 3))
    state = state.applyMove(Play(3, 2))
    state = state.applyMove(Play(2, 2))
    state = state.applyMove(Play(4, 2))
    state = state.applyMove(Play(2, 1))
    state = state.applyMove(Play(4, 4))
    state = state.applyMove(Play(5, 3))
    state = state.applyMove(Play(5, 2))
    state = state.applyMove(Play(2, 4))
    state = state.applyMove(Play(3, 5))
    state = state.applyMove(Play(1, 4))
    state = state.applyMove(Play(5, 4))
    state = state.applyMove(Play(1, 2))
    println(state.board)
    state
  }

  // 5 ....O
  // 4 .O.XO
  // 3 .XXO.
  // 2 .X.XO
  // 1 .....
  //   ABCDE
  private def create5x5GameStateWithKo(): GameState = {
    var state = GameState(GoBoard(5), BlackPlayer)
    state = state.applyMove(Play(3, 3))
    state = state.applyMove(Play(3, 4))
    state = state.applyMove(Play(2, 4))
    state = state.applyMove(Play(2, 5))
    state = state.applyMove(Play(4, 4))
    state = state.applyMove(Play(1, 5))
    state = state.applyMove(Play(4, 2))
    state = state.applyMove(Play(4, 5))
    state = state.applyMove(Play(3, 5)) // black takes the ko initially
    state = state.applyMove(Play(2, 2)) // white threat
    state = state.applyMove(Play(3, 2)) // black responds
    state = state.applyMove(Play(3, 4)) // white retakes the ko
    println(state)
    state
  }
}
