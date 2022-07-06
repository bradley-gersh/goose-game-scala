package it.scalalearn.goosegame.internal.commandlogic

import it.scalalearn.goosegame.internal.events.{Event, PlayerAdded}
import it.scalalearn.goosegame.internal.gamestate.{GameState, GameStateUpdater}
import it.scalalearn.goosegame.ui.errors.{DoubledPlayerError, GameError}

object RosterScriptWriter {
  def addPlayer(gameState: GameState, name: String): Either[GameError, List[Event]] =
    if (gameState.hasPlayer(name))
      Left(DoubledPlayerError(name))
    else
      Right(List(PlayerAdded(name)))
}