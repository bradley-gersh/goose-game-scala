package it.scalalearn.goosegame.gamestate

import it.scalalearn.goosegame.errors.{GameError, UnknownPlayerError}
import it.scalalearn.goosegame.gamestate.SpecialSquares.START_SQUARE

case class GameState(playerSquares: Map[String, Int] = Map()) {
  def players: Iterable[String] = playerSquares.keys

  def hasPlayer(name: String): Boolean = playerSquares.contains(name)

  def getPlayerSquare(name: String): Either[GameError, Int] =
    playerSquares.get(name).toRight(UnknownPlayerError(name))

  def playersOnSquare(name: String, square: Int): List[String] = playerSquares.foldLeft(List[String]()) {
    case (otherPlayers, (otherName, otherSquare)) if square == otherSquare && name != otherName => otherPlayers :+ otherName
    case (otherPlayers, _) => otherPlayers
  }
}

case object GameState {
  def apply(gameState: GameState, name: String): GameState = apply(gameState, name, START_SQUARE)

  def apply(gameState: GameState, name: String, square: Int): GameState =
    GameState(gameState.playerSquares + (name -> square))
}