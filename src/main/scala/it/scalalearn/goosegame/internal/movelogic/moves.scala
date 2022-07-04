package it.scalalearn.goosegame.internal.movelogic

case class RawMove(name: String, previousSquare: Int, startSquare: Int, dice: List[Int])

case class ProjectedMove(moveType: MoveType, square: Int)
