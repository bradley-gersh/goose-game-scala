package it.scalalearn.goosegame

object GameStateChanger {
  def addPlayer(gameState: GameState, name: String): Either[String, (GameState, String)] = {
    if (gameState.hasPlayer(name)) {
      Left(s"$name: already existing player")
    } else {
      val newGameState = GameState(gameState, name)
      Right(newGameState, s"players: ${newGameState.players.mkString(", ")}")
    }
  }

  def move(gameState: GameState, name: String, newSquare: Int): GameState = GameState(gameState, name, newSquare)
}
