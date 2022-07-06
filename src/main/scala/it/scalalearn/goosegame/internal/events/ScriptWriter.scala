package it.scalalearn.goosegame.internal.events

import it.scalalearn.goosegame.internal.gamestate.GameState
import it.scalalearn.goosegame.ui.cli.{AddPlayer, Command, MovePlayer}
import it.scalalearn.goosegame.ui.errors.GameError

object ScriptWriter {
  def writeEvents(gameState: GameState, command: Command): Either[GameError, List[Event]] = command match {
    case AddPlayer(name) => RosterScriptWriter.addPlayer(gameState, name)
    case MovePlayer(name, dice) => MoveScriptWriter.movePlayer(gameState, name, dice)
  }
}
