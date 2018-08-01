package org.deeplearning4j.scalphagozero.encoders
import org.deeplearning4j.scalphagozero.board._
import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.factory.Nd4j
import org.nd4j.linalg.indexing.NDArrayIndex

/**
  * AlphaGo Zero Go board encoder, an eleven-plane encoder structured as follows:
  *
  * Planes 0 - 3: Our stones with 1, 2, 3 and 4+ liberties
  * Planes 4 - 7: Opponent stones with 1, 2, 3 and 4+ liberties
  * Plane      8: All ones if we get Komi
  * Plane      9: All ones if opponent gets Komi
  * Plane     10: Indicates moves illegal due to Ko
  *
  * @author Max Pumperla
  */
class ZeroEncoder(override val boardHeight: Int, override val boardWidth: Int)
    extends Encoder(boardHeight, boardWidth, 11) {

  override def name(): String = "AlphaGoZero"

  /**
    * Encode the current game state as board tensor
    *
    * @param gameState GameState instance
    * @return Board tensor representation of the game state
    */
  override def encode(gameState: GameState): INDArray = {

    val tensor = Nd4j.zeros(this.shape(): _*)

    val nextPlayer: Player = gameState.nextPlayer
    nextPlayer.color match {
      case PlayerColor.white => tensor.putSlice(8, Nd4j.ones(boardHeight, boardWidth));
      case PlayerColor.black => tensor.putSlice(8, Nd4j.ones(boardHeight, boardWidth));
    }
    for (row <- 0 until this.boardHeight) {
      for (col <- 0 until this.boardWidth) {
        val p = Point(row + 1, col + 1)
        val goString: Option[GoString] = gameState.board.getGoString(p)

        goString match {
          case None => {
            if (gameState.doesMoveViolateKo(nextPlayer, Move.play(p)))
              tensor.put(Array(10, row, col), Nd4j.scalar(1))
          }
          case Some(string) => {
            var libertyPlane = Math.min(4, string.numLiberties) - 1
            if (string.color.equals(nextPlayer.color))
              libertyPlane += 4
            tensor.put(Array(libertyPlane, row, col), Nd4j.scalar(1))
          }
        }
      }
    }
    tensor
  }

  override def encodeMove(move: Move): Int =
    if (move.isPlay)
      boardHeight * move.point.get.row + move.point.get.col
    else if (move.isPass)
      boardHeight * boardWidth
    else
      throw new IllegalArgumentException("Cannot encode resign move")

  override def decodeMoveIndex(index: Int): Move = {
    if (index.equals(boardWidth * boardHeight))
      Move.pass()
    val row = index / boardHeight
    val col = index % boardHeight
    Move.play(Point(row + 1, col + 1))
  }

}

object ZeroEncoder {

  def apply(boardHeight: Int, boardWidth: Int): ZeroEncoder =
    new ZeroEncoder(boardHeight, boardWidth)
}
