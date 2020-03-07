package org.deeplearning4j.scalphagozero.gtp

import org.lisoft.gonector.{ GoEngine, Move, Player }
import scala.util.Random

// Implementation ported from example provided in README of https://github.com/EmilyBjoerk/gonector
case class RandomGoEngine() extends GoEngine {
  private var size = 0
  final private val rng = new Random()
  private var board: Array[Player] = _

  override def getName = "Random Engine"
  override def getVersion = "0.0.1"

  override def resizeBoard(aSize: Int): Boolean = {
    size = aSize
    true
  }

  override def newGame(): Unit =
    board = new Array[Player](size * size)

  override def setKomi(komi: Float): Unit = {
    // This bot doesn't care about komi.
  }

  def addMove(aMove: Move, aPlayer: Player): Boolean = {
    if ((aMove eq Move.PASS) || (aMove eq Move.RESIGN))
      return true
    val i = aMove.x + aMove.y * size
    if (null != board(i))
      return false
    board(i) = aPlayer
    // we'll ignore ko, hopefully we won't randomly play a ko-fight...
    true
  }

  def nextMove(player: Player): Move = {
    var c: Move = Move.PASS
    var success = false
    var triesLeft = 3
    do {
      c = new Move(rng.nextInt(size), rng.nextInt(size))
      success = addMove(c, player)
      triesLeft -= 1
    } while (!success && triesLeft >= 0)
    if (!success) return Move.RESIGN
    c
  }
}
