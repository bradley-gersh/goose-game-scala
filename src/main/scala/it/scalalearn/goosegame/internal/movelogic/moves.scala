package it.scalalearn.goosegame.internal.movelogic

import it.scalalearn.goosegame.internal.gamestate.SpecialSquares.{BRIDGE_SQUARE, LAST_SQUARE}

sealed trait Move {
  def name: String
  def endSquare: Int
}

case class Bounce(name: String, endSquare: Int) extends Move
case class Goose(name: String, endSquare: Int) extends Move
case class Stop(name: String, endSquare: Int) extends Move
case class Bridge(name: String, endSquare: Int = BRIDGE_SQUARE) extends Move
case class Win(name: String, endSquare: Int = LAST_SQUARE) extends Move