package it.scalalearn.goosegame.readout

import scala.collection.mutable

object ReadoutBuilder {
  def startTurn(name: String, dice: List[Int], oldSquare: Int) =
    new mutable.StringBuilder(
      s"$name rolls ${dice.mkString(", ")}. $name moves from ${if (oldSquare == 0) "Start" else oldSquare} to ")
}
