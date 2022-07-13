package it.scalalearn.goosegame.internal.gamestate

import it.scalalearn.goosegame.internal.gamestate.SpecialSquares.FirstSquare
import it.scalalearn.goosegame.ui.errors.{GameError, UnknownPlayerError}

case class GameState(playerSquares: Map[String, Int] = Map()) {
  val players: List[String] = playerSquares.keys.toList

  def hasPlayer(name: String): Boolean = playerSquares.contains(name)

  def getPlayerSquare(name: String): Either[GameError, Int] =
    playerSquares.get(name).toRight(UnknownPlayerError(name))

  def playersOnSquare(name: String, square: Int): List[String] = playerSquares.collect {
    case (otherName, `square`) if otherName != name => otherName
  }.toList
}

case object GameState {
  def addPlayer(gameState: GameState, name: String): GameState =
    GameState(gameState.playerSquares + (name -> FirstSquare))

  def updatePlayerSquare(gameState: GameState, name: String, square: Int): GameState =
    GameState(gameState.playerSquares + (name -> square))

  def reset: GameState = GameState()
}