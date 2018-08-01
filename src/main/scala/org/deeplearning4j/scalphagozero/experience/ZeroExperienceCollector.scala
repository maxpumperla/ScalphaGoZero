package org.deeplearning4j.scalphagozero.experience

import org.deeplearning4j.scalphagozero.board.GameState

import scala.collection.mutable.ListBuffer

/**
  * Experience collector for AlphaGo Zero games
  *
  * @author Max Pumperla
  */
class ZeroExperienceCollector { // TODO: fix me, needs to be all ND4J arrays

  var states: ListBuffer[GameState] = ListBuffer()
  var visitCounts: ListBuffer[Int] = ListBuffer()
  var rewards: ListBuffer[Double] = ListBuffer()
  private var currentEpisodeStates: ListBuffer[GameState] = ListBuffer()
  private var currentEpisodeVisitCounts: ListBuffer[Int] = ListBuffer()

  private def clearBuffers(): Unit = {
    currentEpisodeStates = ListBuffer()
    currentEpisodeVisitCounts = ListBuffer()
  }

  def beginEpisode(): Unit =
    clearBuffers()

  def recordDecision(gameState: GameState, visitCounts: List[Int]): Unit = {
    currentEpisodeStates += gameState
    currentEpisodeVisitCounts ++= visitCounts
  }

  def completeEpisode(reward: Double): Unit = {
    states ++= currentEpisodeStates
    visitCounts ++= currentEpisodeVisitCounts
    for (_ <- 1 to currentEpisodeStates.size)
      rewards += reward
    clearBuffers()

  }

}
