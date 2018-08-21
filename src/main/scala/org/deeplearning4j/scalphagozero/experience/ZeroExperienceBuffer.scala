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
import org.nd4j.linalg.factory.Nd4j

/**
  * AlphaGo Zero experience buffer combines states, visit counts and rewards as single INDArrays
  *
  * @param states encoded game states as INDArray
  * @param visitCounts visitCounts as INDArray
  * @param rewards rewards as INDArray
  *
  * @author Max Pumperla
  */
class ZeroExperienceBuffer(val states: INDArray, val visitCounts: INDArray, val rewards: INDArray) {}

object ZeroExperienceBuffer {

  def combineExperience(buffers: List[ZeroExperienceCollector]): ZeroExperienceBuffer = {
    val states = buffers.flatMap(_.states)
    val rewards = buffers.flatMap(_.rewards)
    val visitCounts = buffers.flatMap(_.visitCounts)

    val combinedStates = Nd4j.concat(0, states: _*)
    val combinedRewards = Nd4j.concat(0, rewards: _*)
    val combinedVisitCounts = Nd4j.concat(0, visitCounts: _*)

    new ZeroExperienceBuffer(combinedStates, combinedVisitCounts, combinedRewards)

  }
}
