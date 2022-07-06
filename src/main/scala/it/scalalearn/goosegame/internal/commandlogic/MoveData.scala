package it.scalalearn.goosegame.internal.commandlogic

case class MoveData(name: String, previousSquare: Int, startSquare: Int, dice: List[Int])
