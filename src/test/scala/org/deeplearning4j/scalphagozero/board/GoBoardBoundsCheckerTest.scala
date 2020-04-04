package org.deeplearning4j.scalphagozero.board

import org.scalatest.funspec.AnyFunSpec

class GoBoardBoundsCheckerTest extends AnyFunSpec {

  describe("Verify piece played on the board") {
    val boundsChecker = GoBoardBoundsChecker.get(9)

    it("is within the board boundary") {
      assert(boundsChecker.inBounds(Point(2, 2)))
      assert(boundsChecker.inBounds(Point(1, 1)))
      assert(boundsChecker.inBounds(Point(9, 9)))
      assert(boundsChecker.inBounds(Point(1, 9)))
    }
  }

  describe("Verify piece played outside the board") {
    val boundsChecker = GoBoardBoundsChecker.get(9)

    it("is not within the board boundary") {
      assert(!boundsChecker.inBounds(Point(0, 2)))
      assert(!boundsChecker.inBounds(Point(1, 0)))
      assert(!boundsChecker.inBounds(Point(10, 9)))
      assert(!boundsChecker.inBounds(Point(8, 10)))
      assert(!boundsChecker.inBounds(Point(0, 0)))
      assert(!boundsChecker.inBounds(Point(-3, 10)))
      assert(!boundsChecker.inBounds(Point(102, 140)))
    }
  }

  describe("Verify piece played not on the edge") {
    val boundsChecker = GoBoardBoundsChecker.get(9)

    it("is not on the edge") {
      assert(!boundsChecker.isEdge(Point(2, 2)))
      assert(!boundsChecker.isEdge(Point(5, 5)))
      assert(!boundsChecker.isEdge(Point(8, 7)))
      assert(!boundsChecker.isEdge(Point(-3, 10)))
      assert(!boundsChecker.isEdge(Point(102, 140)))
    }
  }

  describe("Verify piece played on the edge") {
    val boundsChecker = GoBoardBoundsChecker.get(9)

    it("is on the edge") {
      assert(boundsChecker.isEdge(Point(1, 7)))
      assert(boundsChecker.isEdge(Point(7, 1)))
      assert(boundsChecker.isEdge(Point(9, 5)))
      assert(boundsChecker.isEdge(Point(4, 9)))
      assert(boundsChecker.isEdge(Point(9, 9)))
      assert(boundsChecker.isEdge(Point(1, 1)))
    }
  }

  describe("Verify piece played not in a corner") {
    val boundsChecker = GoBoardBoundsChecker.get(9)

    it("is not in a corner") {
      assert(!boundsChecker.isCorner(Point(2, 2)))
      assert(!boundsChecker.isCorner(Point(5, 5)))
      assert(!boundsChecker.isCorner(Point(8, 7)))
      assert(!boundsChecker.isCorner(Point(-3, 10)))
      assert(!boundsChecker.isCorner(Point(102, 140)))
    }
  }

  describe("Verify piece played in a corner") {
    val boundsChecker = GoBoardBoundsChecker.get(9)

    it("is in a corner") {
      assert(boundsChecker.isCorner(Point(1, 1)))
      assert(boundsChecker.isCorner(Point(9, 9)))
      assert(boundsChecker.isCorner(Point(9, 1)))
      assert(boundsChecker.isCorner(Point(1, 9)))
    }
  }
}
