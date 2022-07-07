package it.scalalearn.goosegame.internal.gamestate

import it.scalalearn.goosegame.internal.gamestate.SpecialSquares.FirstSquare
import it.scalalearn.goosegame.ui.errors.{GameError, UnknownPlayerError}

case class GameState(playerSquares: Map[String, Int] = Map()) {
  val players: List[String] = playerSquares.keys.toList

  def hasPlayer(name: String): Boolean = playerSquares.contains(name)

  def getPlayerSquare(name: String): Either[GameError, Int] =
    playerSquares.get(name).toRight(UnknownPlayerError(name))

  def playersOnSquare(name: String, square: Int): List[String] = playerSquares.foldLeft(List[String]()) {
    case (otherPlayers, (otherName, otherSquare)) if square == otherSquare && name != otherName => otherPlayers :+ otherName
    case (otherPlayers, _) => otherPlayers
  }
}

case object GameState {
  def apply(gameState: GameState, name: String): GameState = apply(gameState, name, FirstSquare)

  def apply(gameState: GameState, name: String, square: Int): GameState =
    GameState(gameState.playerSquares + (name -> square))
}