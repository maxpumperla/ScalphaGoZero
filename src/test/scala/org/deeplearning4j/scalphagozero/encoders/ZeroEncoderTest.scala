package org.deeplearning4j.scalphagozero.encoders

import org.deeplearning4j.scalphagozero.board.{ Move, Point }
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

}
