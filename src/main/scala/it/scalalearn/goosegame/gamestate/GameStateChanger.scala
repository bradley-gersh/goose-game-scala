package it.scalalearn.goosegame.gamestate

import it.scalalearn.goosegame.readout._

object GameStateChanger {
  def addPlayer(gameState: GameState, name: String): Either[ErrorReadout, (GameState, Readout)] = {
    if (gameState.hasPlayer(name)) {
      Left(ErrorReadout(s"$name: already existing player"))
    } else {
      val newGameState = GameState(gameState, name)
      Right(newGameState, Readout(s"players: ${newGameState.players.mkString(", ")}"))
    }
  }

  def movePlayer(gameState: GameState, name: String, newSquare: Int): GameState = GameState(gameState, name, newSquare)
}
