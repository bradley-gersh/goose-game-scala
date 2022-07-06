package it.scalalearn.goosegame.ui.output

import it.scalalearn.goosegame.internal.events.{Bounce, Bridge, Event, Goose, Move, PlayerAdded, Prank, Roll, Stop, Win}
import it.scalalearn.goosegame.internal.gamestate.GameState
import it.scalalearn.goosegame.ui.output.OutputMessages._

object OutputBuilder {
  def transcribe(gameState: GameState, events: List[Event]): Output = {
    Output(events.map(event => eventMessage(gameState, event)).mkString(""))
  }

  def eventMessage(gameState: GameState, event: Event): String = {
    event match {
      case PlayerAdded(_) => LIST_PLAYERS_MSG(gameState)
      case Bounce(name, _) => MID_ROLL_BOUNCE_MSG(name)
      case Bridge(name, _) => MID_ROLL_BRIDGE_MSG(name)
      case Goose(name, endSquare) => MID_ROLL_GOOSE_MSG(name, endSquare)
      case Prank(name, startSquare, endSquare) => MID_ROLL_PRANK_MSG(name, startSquare, endSquare)
      case Roll(name, startSquare, dice) => START_ROLL_MSG(name, startSquare, dice)
      case Stop(_, endSquare) => endSquare.toString
      case Win(name, _) => WIN_MSG(name)
    }
  }
}
