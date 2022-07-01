package it.scalalearn.goosegame.gamestate

import it.scalalearn.goosegame.errors.{DoubledPlayerError, GameError}
import it.scalalearn.goosegame.gamestate.GameState
import it.scalalearn.goosegame.readout.Readout

object GameStateChanger {
  def addPlayer(gameState: GameState, name: String): Either[GameError, (GameState, Readout)] = {
    if (gameState.hasPlayer(name)) {
      Left(DoubledPlayerError(name))
    } else {
      val newGameState = GameState(gameState, name)
      Right(newGameState, Readout(s"players: ${newGameState.players.mkString(", ")}"))
    }
  }

  def movePlayer(gameState: GameState, name: String, newSquare: Int): GameState = GameState(gameState, name, newSquare)
}
