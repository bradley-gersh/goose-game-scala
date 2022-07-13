package it.scalalearn.goosegame.ui.output

import it.scalalearn.goosegame.internal.events.Dice
import it.scalalearn.goosegame.internal.gamestate.GameState
import it.scalalearn.goosegame.internal.gamestate.SpecialSquares.{BridgeEnd, LastSquare}

object OutputMessages {
  final val ExitMsg: String = "goodbye\n"

  def EndRollStopMsg(newSquare: Int): String =
    newSquare.toString

  def ListPlayersMsg(gameState: GameState): String =
    gameState.players.mkString("players: ", ", ", "")

  def MidRollBounceMsg(name: String): String =
    s"$LastSquare. $name bounces! $name returns to "

  def MidRollBridgeMsg(name: String): String =
    s"The Bridge. $name jumps to "

  def MidRollGooseMsg(name: String, newSquare: Int): String =
    s"$newSquare, The Goose. $name moves again and goes to "

  def MidRollPrankMsg(otherPlayer: String, square: Int, startSquare: Int): String =
    s". On $square there is $otherPlayer, who returns to $startSquare"

  def StartRollMsg(name: String, startSquare: Int, dice: Dice): String =
    s"$name rolls ${dice.values.mkString(", ")}. $name moves from ${if (startSquare == 0) "Start" else startSquare} to "

  def WinMsg(name: String): String = s"$LastSquare. $name Wins!!"
}
