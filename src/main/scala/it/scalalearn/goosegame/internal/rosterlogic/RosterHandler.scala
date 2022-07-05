package it.scalalearn.goosegame.internal.rosterlogic

import it.scalalearn.goosegame.internal.gamestate.GameState
import it.scalalearn.goosegame.ui.errors.{DoubledPlayerError, GameError}
import it.scalalearn.goosegame.ui.output.{FinalOutput, OutputBuilder}

object RosterHandler {
  def addPlayer(gameState: GameState, name: String): Either[GameError, (GameState, FinalOutput)] = {
    if (gameState.hasPlayer(name)) {
      Left(DoubledPlayerError(name))
    } else {
      val gameStateWithNewPlayer = GameState(gameState, name)
      Right(gameStateWithNewPlayer, OutputBuilder.logAddPlayer(gameStateWithNewPlayer).seal())
    }
  }
}
