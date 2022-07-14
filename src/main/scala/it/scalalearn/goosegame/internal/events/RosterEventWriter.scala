package it.scalalearn.goosegame.internal.events

import it.scalalearn.goosegame.internal.gamestate.GameState
import it.scalalearn.goosegame.ui.errors.{DoubledPlayerError, GameError}

object RosterEventWriter {
  def addPlayer(gameState: GameState, name: String): Either[GameError, List[Event]] =
    if (gameState.hasPlayer(name))
      Left(DoubledPlayerError(name))
    else
      Right(List(PlayerAdded(name)))
}
