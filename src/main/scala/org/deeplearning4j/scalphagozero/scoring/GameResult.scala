package org.deeplearning4j.scalphagozero.scoring

import org.deeplearning4j.scalphagozero.board._

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/**
  * Compute the result of a game
  *
  * @param blackPoints points black scored
  * @param whitePoints points white scored
  * @param komi the komi that was agreed to at the beginning of the game
  *
  * @author Max Pumperla
  */
final case class GameResult(blackPoints: Int, whitePoints: Int, komi: Double) {

  val winner: Player = if (blackPoints > whitePoints + komi) BlackPlayer else WhitePlayer

  val winningMargin: Double = Math.abs(blackPoints - (whitePoints + komi))

  override def toString: String = {
    val white = whitePoints + komi
    winner match {
      case BlackPlayer => "B+ " + (blackPoints - white)
      case WhitePlayer => "W+ " + (white - blackPoints)
    }
  }
}

object GameResult {

  /**
    * Compute the game result from the current state.
    *
    * @param gameState GameState instance
    * @return GameResult object
    */
  def computeGameResult(gameState: GameState): GameResult = {
    val territory = evaluateTerritory(gameState.board)
    new GameResult(
      territory.numBlackTerritory + territory.numBlackStones,
      territory.numWhiteStones + territory.numWhiteStones,
      7.5
    )
  }

  /**
    * Evaluate / estimate the territory currently on
    * the Go board
    *
    * @param goBoard GoBoard instance
    * @return Territory object
    */
  def evaluateTerritory(goBoard: GoBoard): Territory = {
    val statusMap = new mutable.HashMap[Point, String]()
    for (row <- 1 to goBoard.row) {
      for (col <- 1 to goBoard.col) {
        val point = Point(row, col)
        if (!statusMap.contains(point)) {
          val stoneColor: Option[Player] = goBoard.getPlayer(point)
          if (stoneColor.isDefined) {
            val color = stoneColor.get
            val status = if (color == BlackPlayer) "black" else "white"
            statusMap.put(point, status)
          } else {
            val (group, neighbors) = collectRegion(point, goBoard)
            var fillWith: String = ""
            if (neighbors.size == 1) {
              val neighborColor: Player = neighbors.head
              val stoneString = if (neighborColor == BlackPlayer) "b" else "w"
              fillWith = "territory_" + stoneString
            } else {
              fillWith = "dame"
            }
            for (position <- group) {
              statusMap.put(position, fillWith)
            }
          }
        }
      }
    }
    new Territory(statusMap)
  }

  private def collectRegion(startingPoint: Point, board: GoBoard): (List[Point], Set[Player]) = {
    val initialPlayer = board.getPlayer(startingPoint)

    val visitedPlayers = mutable.Set[Player]()
    val visitedPoints = ListBuffer[Point](startingPoint)

    val nextPoints = mutable.Stack[Point](startingPoint)
    while (nextPoints.nonEmpty) {
      val point = nextPoints.pop()
      val player = board.getPlayer(point)
      player.foreach(visitedPlayers += _)

      if (player == initialPlayer) {
        val nextVisits = point.neighbors.filter(board.isOnGrid).diff(visitedPoints)
        nextPoints.pushAll(nextVisits)
        visitedPoints += point
      }
    }

    (visitedPoints.toList, visitedPlayers.toSet)
  }
}
