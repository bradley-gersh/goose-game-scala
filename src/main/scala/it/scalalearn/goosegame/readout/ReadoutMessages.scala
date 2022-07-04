package it.scalalearn.goosegame.readout

import it.scalalearn.goosegame.gamestate.GameState
import it.scalalearn.goosegame.gamestate.SpecialSquares.{BRIDGE_END, LAST_SQUARE}

object ReadoutMessages {
  def LIST_PLAYERS_MSG(gameState: GameState): String =
    gameState.players.mkString("players: ", ", ", "")

  def MID_ROLL_BOUNCE_MSG(name: String, bounceToSquare: Int): String =
    s"$LAST_SQUARE. $name bounces! $name returns to $bounceToSquare"

  def MID_ROLL_BRIDGE_MSG(name: String): String =
    s"The Bridge. $name jumps to $BRIDGE_END"

  def MID_ROLL_GOOSE_CONTINUE_MSG(name: String): String =
    s". $name moves again and goes to "

  def MID_ROLL_GOOSE_START_MSG(newSquare: Int): String =
    s"$newSquare, The Goose"

  def MID_ROLL_NORMAL_SQUARE_MSG(newSquare: Int): String =
    newSquare.toString

  def START_ROLL_MSG(name: String, oldSquare: Int, dice: List[Int]): String =
    s"$name rolls ${dice.mkString(", ")}. $name moves from ${if (oldSquare == 0) "Start" else oldSquare} to "

  def WIN_MSG(name: String): String = s"$LAST_SQUARE. $name Wins!!"
}
