package org.deeplearning4j.scalphagozero.gtp

import org.deeplearning4j.scalphagozero.board.GoBoardSerializer
import org.deeplearning4j.scalphagozero.strip
import org.lisoft.gonector.{ Move, Player }
import org.scalatest.funspec.AnyFunSpec
import scala.util.Random

class ScalphaGoEngineTest extends AnyFunSpec {

  val serializer = new GoBoardSerializer()
  val numLayers = 2

  describe("For Engine genmove black") {
    val engine = createGoEngine
    engine.resizeBoard(5)

    it("has valid next black move") {
      assert(engine.nextMove(Player.BLACK) == new Move(2, 2))
    }
  }

  describe("For Engine genmove white") {
    val engine = createGoEngine
    engine.resizeBoard(5)

    engine.addMove(new Move(2, 3), Player.BLACK)
    engine.addMove(new Move(3, 2), Player.WHITE)
    engine.addMove(new Move(3, 3), Player.BLACK)

    println(serializer.serialize(engine.getGameState.board))

    it("has valid next white move") {
      assert(engine.nextMove(Player.WHITE) == new Move(1, 1))
    }
  }

  describe("For Engine genmove black after many stones placed") {
    val engine = createGoEngine
    engine.resizeBoard(5)

    engine.addMove(new Move(2, 3), Player.BLACK)
    engine.addMove(new Move(3, 2), Player.WHITE)
    engine.addMove(new Move(3, 3), Player.BLACK)
    engine.addMove(new Move(4, 4), Player.WHITE)

    println(serializer.serialize(engine.getGameState.board))

    it("has valid next black move") {
      assert(engine.nextMove(Player.BLACK) == new Move(2, 2))
    }
  }

  describe("Play game with genmoves") {
    val engine = createGoEngine
    engine.resizeBoard(5)

    engine.nextMove(Player.BLACK)
    engine.nextMove(Player.WHITE)
    engine.nextMove(Player.BLACK)
    engine.nextMove(Player.WHITE)
    engine.nextMove(Player.BLACK)
    engine.nextMove(Player.WHITE)

    // println(serializer.serialize(engine.getGameState.board))

    it("has expected board state") {
      assert(
        engine.getGameState.board.toString ==
          strip("""--------
           | 5 .....
           | 4 .....
           | 3 .OXX.
           | 2 .XOO.
           | 1 .....
           |   ABCDE
           |--------""")
      )
    }
  }

  private def createGoEngine = ScalphaGoEngine(numLayers, new Random(1))
}
