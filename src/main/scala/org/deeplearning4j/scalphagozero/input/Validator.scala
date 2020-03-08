package org.deeplearning4j.scalphagozero.input

import org.deeplearning4j.scalphagozero.board.{ Move, Point }
import Validator._

/**
  * Ensures valid input from user
  *
  * @author Barry Becker
  */
case class Validator() {

  def getValidMove(text: String, size: Int): Option[Move] =
    text match {
      case "P" | "p" => Some(Move.Pass)
      case "R" | "r" => Some(Move.Resign)
      case s: String =>
        try getValidPlay(s, size)
        catch {
          case _: Throwable => None
        }
      case _ => None
    }

  private def getValidPlay(s: String, size: Int): Option[Move] = {
    val a =
      if (s.contains(",")) s.split(',')
      else if (s.contains(";")) s.split(";")
      else s.split("")

    val rowIntTxt = a(0).trim
    val colChar = a(1).trim
    if (validInt(rowIntTxt, size) && validChar(colChar, size)) {
      val rowInt = size + 1 - rowIntTxt.toInt
      val colInt = letterToInt(colChar)
      Some(Move.Play(Point(rowInt, colInt)))
    } else None
  }

  private def validInt(txt: String, size: Int): Boolean = {
    val result = txt.trim match {
      case INT_REGEX(str) => str.toInt
      case _              => -1
    }
    result > 0 && result <= size
  }

  private def validChar(txt: String, size: Int): Boolean = {
    val i = letterToInt(txt)
    i > 0 && i <= size
  }

  def invalidNum(answer: String, mini: Double, maxi: Double): Boolean = {
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

object Validator {
  def letterToInt(c: String): Int =
    "ABCDEFGHIJKLMNOPQRSTUVWXYZ".indexOf(c.substring(0).toUpperCase()) + 1

  private val INT_REGEX = """(\d+)""".r
}
