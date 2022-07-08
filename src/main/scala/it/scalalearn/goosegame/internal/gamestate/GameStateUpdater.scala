package it.scalalearn.goosegame.internal.gamestate

import it.scalalearn.goosegame.internal.events.{Bounce, Bridge, Event, Goose, PlayerAdded, Prank, Roll, Stop, Win}
import it.scalalearn.goosegame.ui.errors.{DoubledPlayerError, GameError, UnknownPlayerError}
import it.scalalearn.goosegame.ui.output.OutputBuilder

object GameStateUpdater {
  def updateState(gameState: GameState, events: List[Event]): Either[GameError, GameState] =
    events.foldLeft(Right(gameState): Either[GameError, GameState])((stateOrError, event) =>
      stateOrError.flatMap(previousGameState => applyEvent(previousGameState, event)))

  private def applyEvent(previousGameState: GameState, event: Event): Either[GameError, GameState] =
    event match {
      case PlayerAdded(name) => addPlayer(previousGameState, name)
      case Prank(name, _, endSquare) => updatePlayerSquare(previousGameState, name, endSquare)
      case Stop(name, endSquare) => updatePlayerSquare(previousGameState, name, endSquare)
      case Win(_, _) => resetGameState()
      case _ => Right(previousGameState)
    }

  private def addPlayer(gameState: GameState, name: String): Either[GameError, GameState] = {
    if (gameState.hasPlayer(name))
      Left(DoubledPlayerError(name))
    else
      Right(GameState.addPlayer(gameState, name))
  }

  private def updatePlayerSquare(gameState: GameState, name: String, endSquare: Int): Either[GameError, GameState] = {
    if (gameState.hasPlayer(name))
      Right(GameState.updatePlayerSquare(gameState, name, endSquare))
    else
      Left(UnknownPlayerError(name))
  }

  private def resetGameState(): Either[GameError, GameState] = Right(GameState.reset)
}
