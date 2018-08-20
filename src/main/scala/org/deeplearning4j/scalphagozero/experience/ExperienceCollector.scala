package org.deeplearning4j.scalphagozero.experience

import org.nd4j.linalg.api.ndarray.INDArray

/**
  * Abstract base class for collecting experience data for
  * reinforcement learning
  *
  * @author Max Pumperla
  */
abstract class ExperienceCollector {

  /**
    * Start a new episode (reset states etc.)
    */
  def beginEpisode(): Unit

  /**
    * Store decisions made during the episode,
    * namely the current game state and the visit
    * counts of each position.
    *
    * @param state encoded game state
    * @param visitCounts visit counts for each move on the board
    */
  def recordDecision(state: INDArray, visitCounts: INDArray): Unit

  /**
    * Complete the current episode by providing
    * a reward tensor (will also reset internal
    * states)
    *
    * @param reward reward tensor
    */
  def completeEpisode(reward: INDArray): Unit

}
