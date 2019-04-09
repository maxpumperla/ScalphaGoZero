package org.deeplearning4j.scalphagozero.util

import java.util.Scanner

import org.deeplearning4j.scalphagozero.board.{ Move, Point }
import org.deeplearning4j.scalphagozero.util.Input.{ INT_REGEX, letterToInt }

/**
  * Some convenience methods for gathering input from the user
  *
  * @author Barry Becker
  */
class Input {

  private val scanner: Scanner = new Scanner(System.in)

  def getMoveFromUser(size: Int): Move = {
    println("Enter the coordinates (row,col) of where you would like to play (or P to pass, and R to resign): ")
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
      case "P" | "p" => Move.Pass
      case "R" | "r" => Move.Resign
      case s: String =>
        val a = s.split(',')
        Move.Play(Point(size + 1 - a(0).trim.toInt, letterToInt(a(1).trim)))
    }
  }

  private def validMove(text: String): Boolean = {
    val txt = text.toUpperCase()
    if (txt == "R" || txt == "P")
      return true
    if (text.contains(",")) {
      val a = text.split(',')
      println("first = " + a(0) + " second = " + a(1))
      return validInt(a(0)) && validChar(a(1))
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

  private def validChar(txt: String): Boolean = letterToInt(txt) >= 0

  def getInteger(prompt: String, default: Int = 0, min: Int = 0, max: Int = 10000: Int): Int = {
    println(prompt + s" [$default]")
    val n = getNumber(default.toDouble, min.toDouble, max.toDouble).toInt
    println(n)
    n
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
      case _: NumberFormatException => println(s"Invalid number: $num"); return false
      case _: Throwable             => return false
    }
    num < mini || num > maxi
  }
}

object Input {
  private val INT_REGEX = """(\d+)""".r
  private def letterToInt(c: String): Int =
    "ABCDEFGHIJKLMNOPQRSTUVWXYZ".indexOf(c.substring(0).toUpperCase()) + 1
}
