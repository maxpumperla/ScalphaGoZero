package org.deeplearning4j.scalphagozero.agents

import java.io.File
import java.util.Random
import org.deeplearning4j.nn.graph.ComputationGraph
import org.deeplearning4j.scalphagozero.PATH_PREFIX
import org.deeplearning4j.scalphagozero.board.{ BlackPlayer, GameState, GoBoard }
import org.deeplearning4j.scalphagozero.board.Move.Play
import org.deeplearning4j.scalphagozero.encoders.ZeroEncoder
import org.scalatest.funspec.AnyFunSpec

class MonteCarloPlayerTest extends AnyFunSpec {

  describe("A monte carlo playout from a state given a mock nodeCreator (with random priors)") {
    val nodeCreator = new MockNodeCreator()
    val mcPlayer = MonteCarloPlayer(nodeCreator, new Random(1))

    it("should have playout value of 0 when black winning and white just played") {
      val gameState = createBlackWinning5x5GameState()
      val move = Play(1, 2)

      val treeNode = nodeCreator.createNode(gameState, Some(move))
      val value = mcPlayer.valueFromMCPlayout(treeNode)
      assert(gameState.nextPlayer == BlackPlayer)
      assert(value == 1.0)
    }

    it("should have playout value of 1 when white winning and white just played") {
      val gameState = createWhiteWinning5x5GameState()
      val move = Play(1, 4)

      val treeNode = nodeCreator.createNode(gameState, Some(move))
      val value = mcPlayer.valueFromMCPlayout(treeNode)
      assert(gameState.nextPlayer == BlackPlayer)
      assert(value == -1.0)
    }
  }

  describe("A monte carlo playout from the root using actual model and encoder") {

    val model: ComputationGraph =
      ComputationGraph.load(new File(PATH_PREFIX + "model_size_5_layers_2_test.model"), true)
    val encoder = new ZeroEncoder(5)
    val nodeCreator = new ZeroTreeNodeCreator(model, encoder)
    val mcPlayer = MonteCarloPlayer(nodeCreator, new Random(1))

    it("should have playout value of -1 when black winning and white just played") {
      val gameState = createBlackWinning5x5GameState()
      val move = Play(1, 2)

      val treeNode = nodeCreator.createNode(gameState, Some(move))
      val value = mcPlayer.valueFromMCPlayout(treeNode)
      assert(gameState.nextPlayer == BlackPlayer)
      assert(value == 1.0)
    }

    it("should have playout value of 1 when white winning and white just played") {
      val gameState = createWhiteWinning5x5GameState()
      val move = Play(1, 4)

      val treeNode = nodeCreator.createNode(gameState, Some(move))
      val value = mcPlayer.valueFromMCPlayout(treeNode)
      assert(gameState.nextPlayer == BlackPlayer)
      assert(value == -1.0)
    }
  }

  // 1 .O.OO
  // 2 OOOO.
  // 3 XXX.X
  // 4 .X.X.
  // 5 .XOX.
  //   12345
  private def createBlackWinning5x5GameState(): GameState = {
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
    state = state.applyMove(Play(3, 1))
    state = state.applyMove(Play(1, 5)) // white
    //println(state.board)
    state
  }

  // 1 X..OX
  // 2 XO.O.
  // 3 OOOOO
  // 4 .X.X.
  // 5 .X.XX
  //   12345
  private def createWhiteWinning5x5GameState(): GameState = {
    var state = GameState(GoBoard(5), BlackPlayer)
    state = state.applyMove(Play(5, 2))
    state = state.applyMove(Play(3, 1)) // white
    state = state.applyMove(Play(5, 5))
    state = state.applyMove(Play(3, 2)) // white
    state = state.applyMove(Play(4, 2))
    state = state.applyMove(Play(3, 3)) // white
    state = state.applyMove(Play(4, 4))
    state = state.applyMove(Play(3, 4)) // white
    state = state.applyMove(Play(1, 1))
    state = state.applyMove(Play(3, 5)) // white
    state = state.applyMove(Play(1, 5))
    state = state.applyMove(Play(2, 2)) // white
    state = state.applyMove(Play(2, 1))
    state = state.applyMove(Play(2, 4)) // white
    state = state.applyMove(Play(5, 4))
    state = state.applyMove(Play(1, 4)) // white
    //println(state.board)
    state
  }

}
