package org.deeplearning4j.scalphagozero.board

import org.scalatest.FunSpec

class GridTest extends FunSpec {

  // instance under test
  val grid = Grid()

  describe("update string when adding black stone at 2,2") {
    val string = simpleBlackString()
    val mygrid = grid.updateStringWhenAddingStone(Point(2, 2), string)

    it("should be black at 2, 3") {
      assert(mygrid.getPlayer(Point(2, 3)).contains(BlackPlayer))
      assert(mygrid.getString(Point(2, 3)).contains(string))
    }

    it("should be empty elsewhere") {
      assert(mygrid.getPlayer(Point(1, 2)).isEmpty)
      assert(mygrid.getPlayer(Point(4, 4)).isEmpty)
      assert(mygrid.getString(Point(4, 4)).isEmpty)
    }
  }

  describe("replace string at 2,2") {

    var mygrid = createGridWithStringAt22()

    val newString = mediumWhiteString()
    mygrid = mygrid.replaceString(newString)

    it("should be white at 2, 3 and 3, 3") {
      assert(mygrid.getPlayer(Point(2, 3)).contains(WhitePlayer))
      assert(mygrid.getString(Point(2, 3)).contains(newString))
      assert(mygrid.getPlayer(Point(3, 3)).contains(WhitePlayer))
      assert(mygrid.getString(Point(3, 3)).contains(newString))
    }

    it("should be empty elsewhere") {
      assert(mygrid.getPlayer(Point(4, 3)).isEmpty)
      assert(mygrid.getPlayer(Point(4, 4)).isEmpty)
      assert(mygrid.getString(Point(4, 4)).isEmpty)
    }
  }

  /**
   * Suppose we have a grid like this
   * OOOO
   * OXXO
   *  OO  The string of 2 black stones will be removed, and surrounding white string will have its liberties adjusted
   */
  describe("remove string due to capture and update liberties in remaining string") {

    val black2String = simpleBlackString()
    val white6String = GoString(WhitePlayer,
      Set((1, 1), (1, 2), (1, 3), (2, 1), (2, 4)).map(new Point(_)),
      Set((2, 2), (2, 3), (2, 5), (1, 5), (3, 1), (3, 4)).map(new Point(_))
    )
    val white2String = GoString(WhitePlayer, Set(Point(3, 2), Point(3, 3)), Set(Point(4, 2), Point(4, 3)))
    var mygrid = grid.updateStringWhenAddingStone(Point(1, 2), white6String)

    it("should have a black string should with 2 liberties initially") {
      mygrid = mygrid.updateStringWhenAddingStone(Point(2, 2), black2String)
      // the black string is surrounded but not yet captured/removed
      assert(mygrid.getPlayer(Point(2, 2)).contains(BlackPlayer))
      assert(mygrid.getString(Point(2, 3)).get.numLiberties == 2)
    }

    it("should have a black string with liberties even when surrounded") {
      mygrid = mygrid.updateStringWhenAddingStone(Point(3, 3), white2String)
      println(mygrid)
      // the black string is surrounded but not yet captured/removed. Its liberties are not updated here.
      assert(mygrid.getPlayer(Point(2, 2)).contains(BlackPlayer))
      assert(mygrid.getPlayer(Point(2, 3)).contains(BlackPlayer))
      assert(mygrid.getString(Point(2, 3)).get.numLiberties == 2) // to be captured black string
      assert(mygrid.getString(Point(3, 3)).get.numLiberties == 2) // white string
    }

    it("should have a captured black string that is captured by surrounding white string ") {
      // maps from point to list of neighbors
      val nbrMap = NeighborTables.getNbrTable(5)
      mygrid = mygrid.removeString(black2String, nbrMap)

      assert(mygrid.getPlayer(Point(2, 2)).isEmpty)
      assert(mygrid.getPlayer(Point(2, 3)).isEmpty)
      assert(mygrid.getString(Point(2, 3)).isEmpty)
    }

    it("should have surrounding white strings with more liberties after black's capture") {
      assert(mygrid.getString(Point(1, 2)).get.numLiberties == 6)
      assert(mygrid.getString(Point(3, 3)).get.numLiberties == 4)
      assert(mygrid.getString(Point(4, 4)).isEmpty)
    }
  }

  private def simpleBlackString(): GoString = {
    val stones = Set(Point(2, 2), Point(2, 3))
    val liberties = Set(Point(3, 2), Point(3, 3))
    GoString(BlackPlayer, stones, liberties)
  }

  private def mediumWhiteString(): GoString = {
    val stones = Set(Point(1, 2), Point(2, 2), Point(3, 3), Point(2, 3))
    val liberties = Set(Point(1, 3), Point(2, 4), Point(2, 4), Point(4, 3))
    GoString(WhitePlayer, stones, liberties)
  }

  private def createGridWithStringAt22(): Grid = {
    grid.updateStringWhenAddingStone(Point(2, 2), simpleBlackString())
  }
}
