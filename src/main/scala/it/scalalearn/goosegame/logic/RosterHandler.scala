package it.scalalearn.goosegame.logic

import it.scalalearn.goosegame.errors.{DoubledPlayerError, GameError, UnknownPlayerError}
import it.scalalearn.goosegame.gamestate.GameState
import it.scalalearn.goosegame.readout.{FinalReadout, ReadoutBuilder}

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
