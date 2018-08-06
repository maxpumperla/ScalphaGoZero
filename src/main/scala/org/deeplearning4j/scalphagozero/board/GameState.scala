package org.deeplearning4j.scalphagozero.board

import org.deeplearning4j.scalphagozero.scoring.GameResult
import scala.collection.mutable.ListBuffer

/**
  * GameState encodes the state of a game of Go. Game states have board instances,
  * but also track previous moves to assert validity of moves etc. GameState is
  * immutable, i.e. after you apply a move a new GameState instance will be returned.
  *
  * @param board a GoBoard instance
  * @param nextPlayer the Player who is next to play
  * @param previousState Previous GameState, if any
  * @param lastMove last move played in this game, if any
  *
  * @author Max Pumperla
  */
class GameState(val board: GoBoard,
                val nextPlayer: Player,
                val previousState: Option[GameState],
                val lastMove: Option[Move]) {

  var allPreviousStates: Set[(Player, Long)] = previousState match {
    case None => Set[(Player, Long)]()
    case Some(state) =>
      state.allPreviousStates += ((nextPlayer, state.board.zobristHash))
      state.allPreviousStates
  }

  override def equals(obj: scala.Any): Boolean = {
    if (obj.isInstanceOf[GameState]) {
      val other = obj.asInstanceOf[GameState]
      return this.board == other.board && this.previousState == other.previousState &&
      this.nextPlayer == other.nextPlayer && this.lastMove == other.lastMove &&
      this.allPreviousStates == other.allPreviousStates
    }
    false
  }

  def applyMove(move: Move): GameState = {
    val nextBoard: GoBoard = move.isPlay match {
      case true =>
        val nextBoard = this.board.clone()
        nextBoard.placeStone(nextPlayer, move.point.get)
        nextBoard
      case false => this.board
    }
    new GameState(nextBoard, nextPlayer.other, Some(this), Some(move))

  }

  def isMoveSelfCapture(player: Player, move: Move): Boolean = {
    if (!move.isPlay)
      return false
    this.board.isSelfCapture(player, move.point.get)
  }

  def doesMoveViolateKo(player: Player, move: Move): Boolean = {
    if (!move.isPlay)
      return false
    if (!this.board.willCapture(player, move.point.get))
      return false

    val nextBoard = this.board.clone()
    nextBoard.placeStone(player, move.point.get)
    val nextSituation = (player.other, nextBoard.zobristHash)
    this.allPreviousStates.contains(nextSituation)

  }

  def situation: (Player, GoBoard) = (nextPlayer, board)

  def isValidMove(move: Move): Boolean = {
    if (this.isOver)
      return false
    if (move.isPass || move.isResign)
      return true
    this.board.getColor(move.point.get).isDefined &&
    !this.isMoveSelfCapture(nextPlayer, move) &&
    !this.doesMoveViolateKo(nextPlayer, move)
  }

  def isOver: Boolean = {
    if (this.lastMove.isEmpty)
      return false
    if (this.lastMove.get.isResign)
      return true
    val secondLastMove = this.previousState.get.lastMove
    if (secondLastMove.isEmpty)
      return false
    this.lastMove.get.isPass && secondLastMove.get.isPass
  }

  def legalMoves: List[Move] = {
    if (this.isOver)
      return ListBuffer().toList
    val moves = ListBuffer[Move](Move.pass(), Move.resign())
    for (row <- 1 to board.row) {
      for (col <- 1 to board.col) {
        val move = Move.play(Point(row, col))
        if (this.isValidMove(move))
          moves += move
      }
    }
    moves.toList
  }

  def winner: Option[Int] = {
    if (this.isOver)
      return None
    if (this.lastMove.isDefined && this.lastMove.get.isResign)
      return Some(this.nextPlayer.color)
    val gameResult = GameResult.computeGameResult(this)
    Some(gameResult.winner)
  }

}

object GameState {

  def newGame(boardHeight: Int, boardWidth: Int): GameState = {
    val board = new GoBoard(boardHeight, boardWidth)
    new GameState(board, Player(PlayerColor.black), None, None)
  }

}
