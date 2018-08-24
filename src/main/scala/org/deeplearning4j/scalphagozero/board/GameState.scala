package org.deeplearning4j.scalphagozero.board

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
class GameState(
    val board: GoBoard,
    val nextPlayer: Player,
    val previousState: Option[GameState],
    val lastMove: Option[Move]
) {

  private val allPreviousStates: Set[(Player, Long)] =
    previousState match {
      case None        => Set.empty
      case Some(state) => Set(nextPlayer -> state.board.zobristHash)
    }

  val isOver: Boolean =
    this.lastMove match {
      case None | Some(Move.Play(_)) => false
      case Some(Move.Resign)         => true
      case Some(Move.Pass) =>
        val secondLastMove = this.previousState.get.lastMove
        secondLastMove match {
          case Some(Move.Pass)                               => true
          case None | Some(Move.Play(_)) | Some(Move.Resign) => false
        }
    }

  override def equals(obj: scala.Any): Boolean = {
    obj match {
      case other: GameState =>
        return this.board == other.board && this.previousState == other.previousState &&
        this.nextPlayer == other.nextPlayer && this.lastMove == other.lastMove &&
        this.allPreviousStates == other.allPreviousStates
      case _ =>
    }
    false
  }

  def applyMove(move: Move): GameState = {
    val nextBoard: GoBoard =
      move match {
        case Move.Play(point) =>
          val nextBoard = this.board.clone()
          try {
            nextBoard.placeStone(nextPlayer, point)
          } catch {
            case _: Exception => println(" Illegal move attempted at: " + point.toCoords)
          }
          nextBoard
        case Move.Pass | Move.Resign => this.board
      }

    new GameState(nextBoard, nextPlayer.other, Some(this), Some(move))
  }

  def isMoveSelfCapture(player: Player, move: Move): Boolean =
    move match {
      case Move.Play(point)        => this.board.isSelfCapture(player, point)
      case Move.Pass | Move.Resign => false
    }

  def doesMoveViolateKo(player: Player, move: Move): Boolean =
    move match {
      case Move.Play(point) if this.board.willCapture(player, point) =>
        val nextBoard = this.board.clone()
        nextBoard.placeStone(player, point)
        val nextSituation = (player.other, nextBoard.zobristHash)
        this.allPreviousStates.contains(nextSituation)
      case _ => false
    }

  def isValidMove(move: Move): Boolean =
    if (this.isOver) false
    else {
      move match {
        case Move.Resign | Move.Pass => true
        case Move.Play(point) =>
          this.board.getColor(point).isEmpty &&
          !this.isMoveSelfCapture(nextPlayer, move) &&
          !this.doesMoveViolateKo(nextPlayer, move)
      }
    }

}

object GameState {

  def newGame(boardHeight: Int, boardWidth: Int): GameState = {
    val board = new GoBoard(boardHeight, boardWidth)
    new GameState(board, Player(PlayerColor.Black), None, None)
  }

}
