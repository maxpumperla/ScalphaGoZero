package org.deeplearning4j.scalphagozero.experience

import org.nd4j.linalg.api.ndarray.INDArray

/**
  * Abstract base class for collecting experience data for
  * reinforcement learning
  *
  * @author Max Pumperla
  */
abstract class ExperienceCollector {

  def beginEpisode(): Unit

  def recordDecision(state: INDArray, visitCounts: INDArray): Unit

  def completeEpisode(reward: INDArray): Unit

}
