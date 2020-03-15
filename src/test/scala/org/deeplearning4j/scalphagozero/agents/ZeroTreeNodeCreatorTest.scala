package org.deeplearning4j.scalphagozero.agents

import java.io.File
import org.deeplearning4j.nn.graph.ComputationGraph
import org.deeplearning4j.scalphagozero.PATH_PREFIX
import org.deeplearning4j.scalphagozero.board.Move.Play
import org.deeplearning4j.scalphagozero.board.{ BlackPlayer, GameState, GoBoard, Move }
import org.deeplearning4j.scalphagozero.encoders.ZeroEncoder
import org.deeplearning4j.scalphagozero.strip
import org.scalatest.funspec.AnyFunSpec

class ZeroTreeNodeCreatorTest extends AnyFunSpec {

  describe("zeroTreeNode creation") {

    val model: ComputationGraph =
      ComputationGraph.load(new File(PATH_PREFIX + "model_size_5_layers_2_test.model"), true)
    val encoder = new ZeroEncoder(5)
    val creator = new ZeroTreeNodeCreator(model, encoder)

    val gameState = createSimple5x5GameState()

    val zeroTreeNode = creator.createNode(gameState: GameState)
    it("should create expected zero tree node given gameState") {
      assert(zeroTreeNode.lastMove.isEmpty)
      assert(
        zeroTreeNode.gameState.board.toString ==
          strip("""--------
            | 5 .O.O.
            | 4 OOOO.
            | 3 .XX.X
            | 2 .X.X.
            | 1 .XOX.
            |   ABCDE
            |--------""")
      )
      assert(zeroTreeNode.totalVisitCount == 1)

      val validNextMoves = List(
        Play(5, 1),
        Play(3, 1),
        Play(5, 5),
        Play(4, 1),
        Play(4, 5),
        Play(4, 3),
        Play(1, 5),
        Move.Pass,
        Play(2, 5),
        Play(3, 4)
      )
      assert(zeroTreeNode.moves == validNextMoves)
    }

    it("should not have a child move that is not a valid next move") {
      assert(!zeroTreeNode.branches.keySet.contains(Play(2, 3)))
    }

    it("should have expected values after move(3, 4)") {
      val move = Move.Play(3, 4)
      assert(zeroTreeNode.branches.keySet.contains(move))
      assert(zeroTreeNode.expectedValue(move) == 0)
      assert(zeroTreeNode.visitCount(move) == 0)
      assert(Math.abs(zeroTreeNode.prior(move) - 0.0465302057564) < 0.000001)
    }

    it("should have expected values after move(2, 5)") {
      val move = Move.Play(2, 5)
      assert(zeroTreeNode.branches.keySet.contains(move))
      assert(zeroTreeNode.expectedValue(move) == 0)
      assert(zeroTreeNode.visitCount(move) == 0)
      assert(Math.abs(zeroTreeNode.prior(move) - 0.04731213673949) < 0.000001)
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
    //println(state.board)
    state
  }
}
