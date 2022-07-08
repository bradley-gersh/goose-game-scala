package it.scalalearn.goosegame.internal.events

import it.scalalearn.goosegame.internal.gamestate.GameState
import it.scalalearn.goosegame.ui.errors.GameError

object RosterEventWriter {
  def addPlayer(name: String): Either[GameError, List[Event]] = Right(List(PlayerAdded(name)))
}
