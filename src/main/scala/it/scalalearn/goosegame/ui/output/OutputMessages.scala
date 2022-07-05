package it.scalalearn.goosegame.ui.output

import it.scalalearn.goosegame.internal.gamestate.GameState
import it.scalalearn.goosegame.internal.gamestate.SpecialSquares.{BRIDGE_END, LAST_SQUARE}

object OutputMessages {
  def LIST_PLAYERS_MSG(gameState: GameState): String =
    gameState.players.mkString("players: ", ", ", "")

  def MID_ROLL_BOUNCE_MSG(name: String): String =
    s"$LAST_SQUARE. $name bounces! $name returns to "

  def MID_ROLL_BRIDGE_MSG(name: String): String =
    s"The Bridge. $name jumps to "

  def MID_ROLL_GOOSE_MSG(name: String, newSquare: Int): String =
    s"$newSquare, The Goose. $name moves again and goes to "

  def MID_ROLL_STOP_SQUARE_MSG(newSquare: Int): String =
    newSquare.toString

  def MID_ROLL_PRANK_MSG(otherPlayer: String, square: Int, startSquare: Int): String =
    s". On $square there is $otherPlayer, who returns to $startSquare"

  def START_ROLL_MSG(name: String, startSquare: Int, dice: List[Int]): String =
    s"$name rolls ${dice.mkString(", ")}. $name moves from ${if (startSquare == 0) "Start" else startSquare} to "

  def WIN_MSG(name: String): String = s"$LAST_SQUARE. $name Wins!!"
}
