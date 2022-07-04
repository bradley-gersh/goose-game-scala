package it.scalalearn.goosegame.internal.rosterlogic

import it.scalalearn.goosegame.internal.gamestate.GameState
import it.scalalearn.goosegame.ui.errors.{DoubledPlayerError, GameError}
import it.scalalearn.goosegame.ui.readout.{FinalReadout, ReadoutBuilder}

object RosterHandler {
  def addPlayer(gameState: GameState, name: String): Either[GameError, (GameState, FinalReadout)] = {
    if (gameState.hasPlayer(name)) {
      Left(DoubledPlayerError(name))
    } else {
      val gameStateWithNewPlayer = GameState(gameState, name)
      Right(gameStateWithNewPlayer, ReadoutBuilder.logAddPlayer(gameStateWithNewPlayer).seal())
    }
  }
}
