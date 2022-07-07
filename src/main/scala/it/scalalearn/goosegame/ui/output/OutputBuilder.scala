package it.scalalearn.goosegame.ui.output

import it.scalalearn.goosegame.internal.events.{Bounce, Bridge, Event, Goose, Move, PlayerAdded, Prank, QuitEvent, Roll, Stop, Win}
import it.scalalearn.goosegame.internal.gamestate.GameState
import it.scalalearn.goosegame.ui.output.OutputMessages.*

object OutputBuilder {
  def transcribe(gameState: GameState, events: List[Event]): Output = {
    ConsoleOutput(events.map(event => eventMessage(gameState, event)).mkString(""))
  }

  def eventMessage(gameState: GameState, event: Event): String = {
    event match {
      case PlayerAdded(_) => ListPlayersMsg(gameState)
      case Bounce(name, _) => MidRollBounceMsg(name)
      case Bridge(name, _) => MidRollBridgeMsg(name)
      case Goose(name, endSquare) => MidRollGooseMsg(name, endSquare)
      case Prank(name, startSquare, endSquare) => MidRollPrankMsg(name, startSquare, endSquare)
      case QuitEvent => ExitMsg
      case Roll(name, startSquare, dice) => StartRollMsg(name, startSquare, dice)
      case Stop(_, endSquare) => endSquare.toString
      case Win(name, _) => WinMsg(name)
    }
  }
}
