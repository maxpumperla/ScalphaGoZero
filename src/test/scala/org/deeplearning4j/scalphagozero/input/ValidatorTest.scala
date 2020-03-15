package org.deeplearning4j.scalphagozero.input

import org.deeplearning4j.scalphagozero.board.Move.Play
import org.scalatest.funspec.AnyFunSpec

class ValidatorTest extends AnyFunSpec {

  val validator = new Validator()

  describe("Input 4,C") {

    val expectedResult = Some(Play(2, 3))
    it("should have input = 4C") {
      assertResult(expectedResult) { validator.getValidMove("4,C", 5) }
      assertResult(expectedResult) { validator.getValidMove("4,c", 5) }
      assertResult(expectedResult) { validator.getValidMove("4;C", 5) }
      assertResult(expectedResult) { validator.getValidMove("4C", 5) }
      assertResult(expectedResult) { validator.getValidMove("4c", 5) }
    }
  }

  describe("Invalid input") {

    it("should give None for invalid input") {
      assertResult(None) { validator.getValidMove("XX", 5) }
      assertResult(None) { validator.getValidMove("X", 5) }
      assertResult(None) { validator.getValidMove("", 5) }
      assertResult(None) { validator.getValidMove(",,", 5) }
      assertResult(None) { validator.getValidMove("sdf;sd", 5) }
      assertResult(None) { validator.getValidMove("$a", 5) }
    }

  }
}
