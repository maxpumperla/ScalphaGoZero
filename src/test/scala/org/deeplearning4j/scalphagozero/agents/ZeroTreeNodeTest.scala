package org.deeplearning4j.scalphagozero.agents

import org.deeplearning4j.scalphagozero.board.Move.Play
import org.deeplearning4j.scalphagozero.board._
import org.scalatest.FunSpec

/**
  * @author Barry Becker
  */
class ZeroTreeNodeTest extends FunSpec {

  describe("A root ZeroTreeNode with black to play") {

    val gameState = createSimple5x5GameState()

    // possible black plays
    val priors = Map(
      Play(1, 5) -> 0.02,
      Play(2, 5) -> 0.11,
      Play(3, 1) -> 0.6,
      Play(3, 4) -> 0.3,
      Play(5, 1) -> 0.1,
      Play(3, 4) -> 0.29,
      Play(4, 5) -> 0.21,
      Play(5, 5) -> 0.09,
      Move.Pass -> 0.04
    )
    val node = new ZeroTreeNode(gameState, 0.5, priors, None, None)
    it("Has this state ") {
      assert(node.lastMove.isEmpty)
      assert(node.value == 0.5)
      assert(node.totalVisitCount == 1)
    }
    it("has these expected values for moves") {
      val visitCounts = Map(
        Play(1, 5) -> 0,
        Play(2, 5) -> 0,
        Play(3, 1) -> 0,
        Play(3, 4) -> 0,
        Play(5, 1) -> 0,
        Play(3, 4) -> 0,
        Play(4, 5) -> 0,
        Play(5, 5) -> 0,
        Move.Pass -> 0
      )
      visitCounts.foreach { m =>
        assert(node.expectedValue(m._1) == m._2)
      }
    }
    it("has moves") {
      val expMoves =
        List(
          Play(5, 1),
          Play(3, 1),
          Play(5, 5),
          Play(4, 5),
          Play(1, 5),
          Move.Pass,
          Play(2, 5),
          Play(3, 4)
        )
      assert(node.moves == expMoves)

    }
    it("has child") {
      assert(node.getChild(Play(3, 1)).isEmpty)
    }
    it("has change after record visit") {
      val m = Play(3, 1)
      assert(node.expectedValue(m) == 0.0)
      node.recordVisit(m, 0.49)
      assert(node.expectedValue(m) == 0.49)
      node.recordVisit(m, 0.25)
      assert(node.expectedValue(m) == 0.37) // avg of .25 and .49
    }
    it("serialized") {
      assertResult(
        """WhitePlayer None totVisits:3 val:0.5 numkids:0
          | (Play(5,1) -> Br(0.100), Play(3,1) -> Br(0.600, 2, 0.74), Play(5,5) -> Br(0.090), Play(4,5) -> Br(0.210), Play(1,5) -> Br(0.020), Pass -> Br(0.040), Play(2,5) -> Br(0.110), Play(3,4) -> Br(0.290) )
          |""".stripMargin
      ) {
        node.toString()
      }
    }
  }

  describe("An internal ZeroTreeNode with white to play") {

    var gameState = createSimple5x5GameState()
    gameState = gameState.applyMove(Play(3, 1))
    // 1 .O.O.
    // 2 OOOO.
    // 3 XXX.X
    // 4 .X.X.
    // 5 .XOX.
    //   12345

    // reasonable white plays
    val priors = Map(
      Play(2, 5) -> 0.82,
      Play(3, 4) -> 0.21,
      Play(4, 1) -> 0.1,
      Play(5, 1) -> 0.11,
      Play(5, 5) -> 0.05,
      Play(4, 5) -> 0.19,
      Move.Pass -> 0.04
    )

    val parentNode = new ZeroTreeNode(gameState, 0.5, priors, None, None)

    val nextState = gameState.applyMove(Play(2, 5)) // whhite move
    val node = new ZeroTreeNode(nextState, 0.6, priors - Play(2, 5), Some(parentNode), Some(Play(2, 5)))
    parentNode.addChild(Play(2, 5), node)

    it("Has this state ") {
      assert(node.lastMove.contains(Play(2, 5)))
      assert(node.value == 0.6)
      assert(node.totalVisitCount == 1)
    }
    it("has these expected values for moves") {
      val visitCounts = Map(
        Play(3, 4) -> 0,
        Play(4, 1) -> 0,
        Play(5, 1) -> 0,
        Play(5, 5) -> 0,
        Play(4, 5) -> 0,
        Move.Pass -> 0
      )
      visitCounts.foreach { m =>
        assert(node.expectedValue(m._1) == m._2)
      }
    }
    it("has moves") {
      val expMoves =
        List(
          Play(Point(5, 1)),
          Play(Point(5, 5)),
          Play(Point(4, 1)),
          Play(Point(4, 5)),
          Move.Pass,
          Play(Point(3, 4))
        )
      assert(node.moves == expMoves)

    }
    it("has child") {
      assert(node.getChild(Play(2, 5)).isEmpty)
      assert(node.getChild(Play(3, 4)).isEmpty)
    }
    it("has change after record visit") {
      val m = Play(3, 4)
      assert(node.expectedValue(m) == 0.0)
      node.recordVisit(m, 0.49)
      assert(node.expectedValue(m) == 0.49)
      node.recordVisit(m, 0.25)
      assert(node.expectedValue(m) == 0.37) // avg of .25 and .49
      node.recordVisit(m, 0.16)
      assert(node.expectedValue(m) == 0.3) // avg of .25 and .49
    }

    it("serialized") {
      assertResult(
        """BlackPlayer None totVisits:1 val:0.5 numkids:1
          | (Play(5,1) -> Br(0.110), Play(5,5) -> Br(0.050), Play(4,1) -> Br(0.100), Play(4,5) -> Br(0.190), Pass -> Br(0.040), Play(2,5) -> Br(0.820), Play(3,4) -> Br(0.210) )
          |  WhitePlayer Some(Play(2,5)) totVisits:4 val:0.6 numkids:0
          |   (Play(5,1) -> Br(0.110), Play(5,5) -> Br(0.050), Play(4,1) -> Br(0.100), Play(4,5) -> Br(0.190), Pass -> Br(0.040), Play(3,4) -> Br(0.210, 3, 0.90) )
          |""".stripMargin
      ) {
        parentNode.toString()
      }
    }
  }

  // 1 .O.O.
  // 2 OOOO.
  // 3 .XX.X
  // 4 .X.X.
  // 5 .XOX.
  //   12345
  private def createSimple5x5GameState(): GameState = {
    var state = GameState(GoBoard(5), BlackPlayer)
    state = state.applyMove(Play(3, 3))
    state = state.applyMove(Play(2, 3)) // white
    state = state.applyMove(Play(3, 2))
    state = state.applyMove(Play(2, 2)) // white
    state = state.applyMove(Play(4, 2))
    state = state.applyMove(Play(2, 1)) // white
    state = state.applyMove(Play(4, 4))
    state = state.applyMove(Play(5, 3)) // white
    state = state.applyMove(Play(5, 2))
    state = state.applyMove(Play(2, 4)) // white
    state = state.applyMove(Play(3, 5))
    state = state.applyMove(Play(1, 4)) // white
    state = state.applyMove(Play(5, 4))
    state = state.applyMove(Play(1, 2)) // white
    println(state.board)
    state
  }
}
