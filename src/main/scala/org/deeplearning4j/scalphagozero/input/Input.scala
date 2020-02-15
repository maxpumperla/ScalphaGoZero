package org.deeplearning4j.scalphagozero.input

import java.util.Scanner
import org.deeplearning4j.scalphagozero.board.{ Move, Point }

/**
  * Some convenience methods for gathering input from the user
  *
  * @author Barry Becker
  */
class Input {

  private val scanner: Scanner = new Scanner(System.in)
  private val validator = Validator()

  def getMoveFromUser(size: Int): Move = {
    println("Enter the coordinates (row,col) of where you would like to play (or P to pass, and R to resign): ")
    var text: String = ""
    var validMove: Option[Move] = None
    do {
      text = scanner.nextLine()
      validMove = validator.getValidMove(text, size)
      if (validMove.isEmpty) {
        println(s""""$text" is not a valid coordinate. Try again.""")
      }
    } while (validMove.isEmpty)

    validMove.get
  }

  def getInteger(prompt: String, default: Int = 0, min: Int = 0, max: Int = 10000: Int): Int = {
    println(prompt + s" [$default]")
    val n = getNumber(default.toDouble, min.toDouble, max.toDouble).toInt
    println(n)
    n
  }

  /** @param queryMsg the prompt
    * @param default default value
    * @return line of text
    */
  def textQuery(queryMsg: String, default: String = ""): String = {
    println(queryMsg + s" [$default]:")
    val answer = scanner.nextLine()
    if (answer.isEmpty) default else answer
  }

  /** @param queryMsg the prompt
    * @param alternatives set of possible responses (lower case)
    * @return given a prompt return one of several specified upper case characters from the user
    */
  def charQuery(queryMsg: String, alternatives: Seq[Character], default: Option[Character] = None): Character = {
    val theDefault = if (default.isDefined) default.get else alternatives.head
    println(queryMsg + " " + alternatives.mkString("(", "/", ")"))
    print(s"[$theDefault]:")
    val answer = scanner.nextLine()
    if (answer.isEmpty) theDefault else answer.head.toLower
  }

  /**
    * @param default default value to use if one not provided
    * @param min minimum allowed
    * @param max maximum allowed
    * @return the user specified number.
    */
  def getNumber(default: Double, min: Double, max: Double): Double = {
    var answer = scanner.nextLine()
    if (answer.isEmpty) default
    else {
      while (validator.invalidNum(answer, min, max)) {
        println(s"Invalid number '$answer'. Enter a number between $min and $max, inclusive.")
        answer = scanner.nextLine()
      }
      answer.toDouble
    }
  }
}
