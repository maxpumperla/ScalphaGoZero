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

import scala.collection.mutable.ListBuffer

/**
  * Experience collector for AlphaGo Zero games. Collects encoded game states,
  * visit counts and rewards.
  *
  * @author Max Pumperla
  */
class ZeroExperienceCollector extends ExperienceCollector {

  var states: ListBuffer[INDArray] = ListBuffer()
  var visitCounts: ListBuffer[INDArray] = ListBuffer()
  var rewards: ListBuffer[INDArray] = ListBuffer()
  private var currentEpisodeStates: ListBuffer[INDArray] = ListBuffer()
  private var currentEpisodeVisitCounts: ListBuffer[INDArray] = ListBuffer()

  private def clearBuffers(): Unit = {
    currentEpisodeStates = ListBuffer()
    currentEpisodeVisitCounts = ListBuffer()
  }

  override def beginEpisode(): Unit =
    clearBuffers()

  override def recordDecision(state: INDArray, visitCounts: INDArray): Unit = {
    currentEpisodeStates += state
    currentEpisodeVisitCounts += visitCounts
  }

  override def completeEpisode(reward: INDArray): Unit = {
    states ++= currentEpisodeStates
    visitCounts ++= currentEpisodeVisitCounts
    for (_ <- 1 to currentEpisodeStates.size)
      rewards += reward
    clearBuffers()

  }

}
