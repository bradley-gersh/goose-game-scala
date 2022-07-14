package it.scalalearn.goosegame.internal.events

import it.scalalearn.goosegame.internal.gamestate.GameState
import it.scalalearn.goosegame.ui.cli.{AddPlayer, Command, MovePlayer, Quit}
import it.scalalearn.goosegame.ui.errors.GameError

object EventWriter {
  def writeEvents(gameState: GameState, command: Command): Either[GameError, List[Event]] = command match {
    case AddPlayer(name) => RosterEventWriter.addPlayer(gameState, name)
    case MovePlayer(name, dice) => MoveEventWriter.movePlayer(gameState, name, dice)
    case Quit => Right(List(QuitEvent))
  }
}
