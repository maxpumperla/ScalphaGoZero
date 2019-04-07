package org.deeplearning4j.scalphagozero.util

import java.util.Scanner

import org.deeplearning4j.scalphagozero.board.{ Move, Point }
import org.deeplearning4j.scalphagozero.util.Input.INT_REGEX

/**
  * Some convenience methods for gathering input from the user
  *
  * @author Barry Becker
  */
class Input {

  private val scanner: Scanner = new Scanner(System.in)

  def getMoveFromUser: Move = {
    println("Enter the coordinates (row, col) of where you would like to play (or P to pass, and R to resign): ")
    var text: String = ""
    var valid = false
    do {
      text = scanner.nextLine()
      valid = validMove(text)
      if (!valid) {
        println(text + " is not a valid coordinate. Try again.")
      }
    } while (!valid)

    text match {
      case "P" => Move.Pass
      case "R" => Move.Resign
      case s: String =>
        val a = s.split(',')
        Move.Play(Point(a(0).trim.toInt, a(1).trim.toInt))
    }
  }

  private def validMove(text: String): Boolean = {
    if (text == "R" || text == "P")
      return true
    if (text.contains(",")) {
      val a = text.split(',')
      println("first = " + a(0) + " second = " + a(1))
      return validInt(a(0)) && validInt(a(1))
    }
    false
  }

  private def validInt(txt: String): Boolean = {
    val result = txt.trim match {
      case INT_REGEX(str) => str.toInt
      case _              => -1
    }
    result > 0
  }

  /**
    * @param default default value to use if one not provided
    * @param min minimum allowed
    * @param max maximum allowed
    * @return the user specified number.
    */
  def getNumber(default: Double, min: Double, max: Double): Double = {
    print(s"[${default.toFloat}]")
    var answer = scanner.nextLine()
    if (answer.isEmpty) default
    else {
      while (invalidNum(answer, min, max)) {
        println(s"Invalid number '$answer'. Enter a number between $min and $max, inclusive.")
        answer = scanner.nextLine()
      }
      answer.toDouble
    }
  }

  private def invalidNum(answer: String, mini: Double, maxi: Double): Boolean = {
    var num: Double = 0.0
    try {
      num = answer.trim.toDouble
    } catch {
      case e: NumberFormatException => e; println(s"Invalid number: $num"); false
      case _: Throwable             => false
    }
    num < mini || num > maxi
  }
}

object Input {
  private val INT_REGEX = """(\d+)""".r
}
