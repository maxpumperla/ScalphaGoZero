package org.deeplearning4j.scalphagozero.util

import java.text.DecimalFormat

import clouseau.Calculate

object ObjectSizer {

  private val FORMATTER = new DecimalFormat("###,###")

  /** @return size in KB */
  def getSizeKB(o: Object): String = FORMATTER.format(Calculate.fullSizeOf(o) / 1000) + " KB"
}
