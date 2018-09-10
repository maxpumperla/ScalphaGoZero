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
final case class ZeroExperienceBuffer(states: INDArray, visitCounts: INDArray, rewards: INDArray)

object ZeroExperienceBuffer {

  def combineExperience(buffers: List[ZeroExperienceCollector]): ZeroExperienceBuffer = {
    val states = buffers.flatMap(_.states)
    val rewards = buffers.flatMap(_.rewards)
    val visitCounts = buffers.flatMap(_.visitCounts)

    val combinedStates = Nd4j.concat(0, states: _*)
    val combinedRewards = Nd4j.concat(0, rewards: _*)
    val combinedVisitCounts = Nd4j.concat(0, visitCounts: _*)

    ZeroExperienceBuffer(combinedStates, combinedVisitCounts, combinedRewards)
  }
}
