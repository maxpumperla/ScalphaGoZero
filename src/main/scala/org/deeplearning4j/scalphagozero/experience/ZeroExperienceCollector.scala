package org.deeplearning4j.scalphagozero.experience

import org.nd4j.linalg.api.ndarray.INDArray

import scala.collection.mutable.ListBuffer

/**
  * Experience collector for AlphaGo Zero games. Collects encoded game states,
  * visit counts and rewards.
  *
  * @author Max Pumperla
  */
class ZeroExperienceCollector {

  var states: ListBuffer[INDArray] = ListBuffer()
  var visitCounts: ListBuffer[INDArray] = ListBuffer()
  var rewards: ListBuffer[INDArray] = ListBuffer()
  private var currentEpisodeStates: ListBuffer[INDArray] = ListBuffer()
  private var currentEpisodeVisitCounts: ListBuffer[INDArray] = ListBuffer()

  private def clearBuffers(): Unit = {
    currentEpisodeStates = ListBuffer()
    currentEpisodeVisitCounts = ListBuffer()
  }

  def beginEpisode(): Unit =
    clearBuffers()

  def recordDecision(state: INDArray, visitCounts: List[INDArray]): Unit = {
    currentEpisodeStates += state
    currentEpisodeVisitCounts ++= visitCounts
  }

  def completeEpisode(reward: INDArray): Unit = {
    states ++= currentEpisodeStates
    visitCounts ++= currentEpisodeVisitCounts
    for (_ <- 1 to currentEpisodeStates.size)
      rewards += reward
    clearBuffers()

  }

}
