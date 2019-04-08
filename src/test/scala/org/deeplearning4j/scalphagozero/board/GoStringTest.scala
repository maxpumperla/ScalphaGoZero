package org.deeplearning4j.scalphagozero.board

import org.scalatest.FunSpec

class GoStringTest extends FunSpec {

  describe("Create a simple string") {

    val stones = Set(Point(2, 2))
    val liberties = Set((1, 2), (3, 2), (2, 1), (2, 3)).map(p => new Point(p))

    it("should create simple string with one stone") {
      val goString = GoString(BlackPlayer, stones, liberties)

      assertResult(4) { goString.numLiberties }
      assertResult("GoString(BlackPlayer,Set(Point(2,2)),Set(Point(1,2), Point(3,2), Point(2,1), Point(2,3)))") {
        goString.toString
      }
    }
  }

  describe("Add liberty to string") {

    val stones = Set(Point(2, 2))
    val liberties = Set((1, 2), (3, 2)).map(p => new Point(p))
    it("should have one more liberty") {
      val goString = GoString(BlackPlayer, stones, liberties)
      assert(goString.numLiberties === 2)
      assertResult(3) { goString.withLiberty(Point(2, 1)).numLiberties }
    }
  }

  describe("Remove liberty from string") {

    val stones = Set(Point(2, 2))
    val liberties = Set((1, 2), (3, 2), (2, 1), (2, 3)).map(p => new Point(p))

    it("should have one fewer liberties") {
      val goString = GoString(BlackPlayer, stones, liberties)
      assert(goString.numLiberties === 4)
      var gs = goString.withoutLiberty(Point(2, 1))
      assertResult(3) { gs.numLiberties }
      gs = gs.withoutLiberty(Point(2, 3))
      assertResult(2) { gs.numLiberties }
    }
  }

  describe("Merge strings") {

    it("should have 6 liberties when merged") {
      val stones1 = Set(Point(2, 2))
      val liberties1 = Set((1, 2), (2, 1), (3, 2), (2, 3)).map(p => new Point(p))
      val goString1 = GoString(BlackPlayer, stones1, liberties1)

      val stones2 = Set(Point(2, 3))
      val liberties2 = Set((2, 2), (2, 4), (1, 3), (3, 3)).map(p => new Point(p))
      val goString2 = GoString(BlackPlayer, stones2, liberties2)

      assertResult(4) { goString1.numLiberties }
      assertResult(4) { goString2.numLiberties }
      val mergedString = goString1.mergedWith(goString2)
      assertResult(6) { mergedString.numLiberties }
      assertResult(
        "GoString(BlackPlayer,Set(Point(2,2), Point(2,3)),Set(Point(3,2), Point(1,3), Point(2,4), Point(3,3), Point(1,2), Point(2,1)))"
      ) {
        mergedString.toString
      }
    }
  }

}
