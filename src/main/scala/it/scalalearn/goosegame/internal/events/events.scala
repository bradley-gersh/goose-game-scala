package it.scalalearn.goosegame.internal.events

import it.scalalearn.goosegame.internal.gamestate.SpecialSquares.{BridgeSquare, LastSquare}
import it.scalalearn.goosegame.internal.events.{Event, Move}

sealed trait Event

case class PlayerAdded(name: String) extends Event
case class Roll(name: String, startSquare: Int, dice: List[Int]) extends Event

sealed trait Move extends Event {
  def name: String
  def endSquare: Int
}

case class Bounce(name: String, endSquare: Int) extends Move
case class Bridge(name: String, endSquare: Int = BridgeSquare) extends Move
case class Goose(name: String, endSquare: Int) extends Move
case class Prank(name: String, startSquare: Int, endSquare: Int) extends Move
case class Stop(name: String, endSquare: Int) extends Move
case class Win(name: String, endSquare: Int = LastSquare) extends Move
