package org.deeplearning4j.scalphagozero.agents

import java.io.File

import org.deeplearning4j.nn.graph.ComputationGraph
import org.deeplearning4j.scalphagozero.agents.ZeroAgentTest.RND
import org.deeplearning4j.scalphagozero.board.{ BlackPlayer, GameState, GoBoard }
import org.deeplearning4j.scalphagozero.board.Move.Play
import org.deeplearning4j.scalphagozero.encoders.ZeroEncoder
import org.nd4j.linalg.api.ndarray.INDArray
import org.scalatest.FunSpec
import org.deeplearning4j.scalphagozero.PATH_PREFIX
import scala.collection.mutable.ListBuffer
import scala.util.Random

/**
  * @author Barry Becker
  */
class ZeroAgentTest extends FunSpec {

  describe("A ZeroAgent selecting a move") {

    val model: ComputationGraph =
      ComputationGraph.load(new File(PATH_PREFIX + "model_size_5_layers_2.model"), true)
    val encoder = new ZeroEncoder(5)
    val zeroAgent = new ZeroAgent(model, encoder, roundsPerMove = 20, c = 2.0, rand = RND)

    val gameState = createSimple5x5GameState()
    val move = zeroAgent.selectMove(gameState)

    it("should have selected move") {
      assert(move == Play(2, 5))
    }
  }

  describe("A ZeroAgent selecting and playing 2 moves") {

    val model: ComputationGraph =
      ComputationGraph.load(new File("model_size_5_layers_2.model"), true)
    val encoder = new ZeroEncoder(5)
    val zeroAgent = new ZeroAgent(model, encoder, roundsPerMove = 100, c = 2.0, rand = RND)

    val gameState = createSimple5x5GameState()
    var move = zeroAgent.selectMove(gameState)

    val newGameState = gameState.applyMove(move)
    move = zeroAgent.selectMove(newGameState)

    it("should have selected move") {
      assert(move === Play(3, 4))
    }
    // experience was not accumulates, so rewards, states, visitCounts are empty
    it("collector should have these visitCounts") {
      val a: ListBuffer[INDArray] = zeroAgent.collector.visitCounts
      assert(a.isEmpty)
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

object ZeroAgentTest {
  val RND = new Random(1)
}
