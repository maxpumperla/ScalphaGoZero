/*
 * Copyright 2016 Skymind
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
