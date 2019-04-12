package org.deeplearning4j

package object scalphagozero {

  // Location of test resources
  val PATH_PREFIX = "src/test/resources/org/deeplearning4j/scalphagozero/"

  /** @param s string to process
    * @return the string without the leading margin char and with unix style line endings
    */
  def strip(s: String, newlineChar: String = "\n"): String =
    s.stripMargin.replaceAll(System.lineSeparator, newlineChar)
}
