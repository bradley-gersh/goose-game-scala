package it.scalalearn.goosegame.internal.events

import it.scalalearn.goosegame.internal.gamestate.GameState
import it.scalalearn.goosegame.ui.errors.GameError

object RosterScriptWriter {
  def addPlayer(gameState: GameState, name: String): Either[GameError, List[Event]] =
    Right(List(PlayerAdded(name)))
}
