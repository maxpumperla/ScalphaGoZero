package org.deeplearning4j.scalphagozero.gtp

import java.io.File

import org.deeplearning4j.scalphagozero.agents.ZeroAgent
import org.deeplearning4j.scalphagozero.board.{ BlackPlayer, GameState, WhitePlayer }
import org.deeplearning4j.scalphagozero.scoring.GameResult.DEFAULT_KOMI
import ScalphaGoEngine._
import org.deeplearning4j.nn.graph.ComputationGraph
import org.deeplearning4j.scalphagozero.encoders.ZeroEncoder
import org.lisoft.gonector.{ GoEngine, Player }

import scala.util.Random

object ScalphaGoEngine {
  val DEBUG = false
  val NAME = "ScalphaGoZero"
  val VERSION = "1.0.1"
  val DEFAULT_SIZE = 5

  private val ROUNDS_PER_MOVE = 100
  private val DEFAULT_NUM_LAYERS = 2
}

/**
  * Implement Go Text Protocol (GTP) for ScalphaGoZero robot.
  * See http://www.lysator.liu.se/~gunnar/gtp/
  *
  * The NN model will be loaded from the /models directory and is assumed to be in this form:
  * model_size_<boardSize>_layers_<numLayers>.model
  * If such a model file does not exist for the specified boardSize and numLayers,
  * then an IllegalArgumentException will be thrown.
  *
  * @param numLayers the number of resNet layers to use in the NN.
  * @param rnd random number generator to use. Use this if you need reproducible results - like for testing.
  * @author Barry Becker
  */
case class ScalphaGoEngine(numLayers: Int = DEFAULT_NUM_LAYERS, rnd: Random = new Random()) extends GoEngine {

  private var boardSize = DEFAULT_SIZE
  private var komi = DEFAULT_KOMI

  private var game: GameState = _

  private var encoder: ZeroEncoder = _
  private var model = getModel(numLayers)

  // Create two AGZ opponents based on the same model
  private var blackZeroAgent: ZeroAgent = _
  private var whiteZeroAgent: ZeroAgent = _
  resizeBoard(boardSize)

  /** @return name of the robot */
  def getName: String = NAME

  /** @return A string encoding the version number. Must be ASCII only */
  def getVersion: String = VERSION

  /** @return true if this engine is capable of scoring a game. */
  def canScore = false

  /** @param theKomi The new komi value, any value is allowed.*/
  def setKomi(theKomi: Float): Unit = {
    komi = theKomi
    println("komi = " + komi)
  }

  /**
    * The Go Text Protocol mandates that this is called at least once before
    * the first call to newGame(). It should have no effect if called after newGame().
    * @param size An integer larger in the range [3, 19].
    * @return true if the board size is supported, false otherwise.
    */
  def resizeBoard(size: Int): Boolean =
    if (size < 3 || size > 19) {
      false
    } else {
      boardSize = size
      encoder = ZeroEncoder(boardSize)
      model = getModel(numLayers)

      // Create two AGZ opponents based on the same model
      blackZeroAgent = new ZeroAgent(model, encoder, ROUNDS_PER_MOVE, rand = rnd)
      whiteZeroAgent = new ZeroAgent(model, encoder, ROUNDS_PER_MOVE, rand = rnd)

      // without this call to newGame, it is mandatory that "clear_board" be called.
      newGame()
      true
    }

  /**
    * Starts a new game. The robot should clear its internal state to a new game.
    */
  def newGame(): Unit =
    game = GameState.newGame(boardSize)

  /**
    * Add a move to the current game state.
    * It can be used to initialize the game to a certain point, or to inform the robot of the opponents move.
    * In either case the robot should update its internal state.
    *
    * @param move contains the horizontal and vertical position to add a stone to.
    * @param player The color of the stone.
    * @return true if the move was legal, false otherwise.
    */
  def addMove(move: org.lisoft.gonector.Move, player: Player): Boolean = {
    import org.deeplearning4j.scalphagozero.board

    if (game.nextPlayer == BlackPlayer && player == Player.WHITE ||
        game.nextPlayer == WhitePlayer && player == Player.BLACK) {
      false
    } else {
      val nextMove: board.Move = move.x match {
        case -2 => board.Move.Pass
        case -1 => board.Move.Resign
        case _  => board.Move.Play(move.y + 1, move.x + 1)
      }
      game = game.applyMove(nextMove)
      true
    }
  }

  /**
    * Ask the robot for the next move for the specified player.
    *
    * @param player The player that a move should be constructed for. Either B or W.
    * @return The move the robot would like to play.
    */
  def nextMove(player: Player): org.lisoft.gonector.Move = {
    assert(game != null, "Game object is not yet initialized.")
    val selectedMove = player match {
      case Player.BLACK => blackZeroAgent.selectMove(game)
      case Player.WHITE => whiteZeroAgent.selectMove(game)
      case _            => throw new IllegalArgumentException("unexpected player: " + player)
    }
    import org.deeplearning4j.scalphagozero.board
    val nextMove = selectedMove match {
      case board.Move.Pass    => org.lisoft.gonector.Move.PASS
      case board.Move.Resign  => org.lisoft.gonector.Move.RESIGN
      case board.Move.Play(p) => new org.lisoft.gonector.Move(p.col - 1, p.row - 1)
    }
    val success = addMove(nextMove, player)
    if (success) nextMove else org.lisoft.gonector.Move.RESIGN
  }

  // for debugging
  def getGameState: GameState = game

  /**
    * @return a pre-trained model, or throws IllegalArgumentException if not found.
    */
  private def getModel(numLayers: Int): ComputationGraph = {
    var file: File = null
    val fileName = s"models/model_size_${boardSize}_layers_$numLayers.model"
    try {
      file = new File(fileName)
    } catch {
      case e: Exception => throw new IllegalArgumentException("Could not find model file named " + fileName, e)
    }
    ComputationGraph.load(file, true)
  }
}
