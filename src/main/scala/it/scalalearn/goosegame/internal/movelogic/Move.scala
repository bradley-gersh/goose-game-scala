package it.scalalearn.goosegame.internal.movelogic

case class Move(name: String, previousSquare: Int, startSquare: Int, dice: List[Int])
