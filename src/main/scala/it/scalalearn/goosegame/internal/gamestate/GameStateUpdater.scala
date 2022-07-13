package it.scalalearn.goosegame.internal.gamestate

import it.scalalearn.goosegame.internal.events.{Bounce, Bridge, Event, Goose, PlayerAdded, Prank, Roll, Stop, Win}
import it.scalalearn.goosegame.ui.errors.{DoubledPlayerError, GameError, UnknownPlayerError}
import it.scalalearn.goosegame.ui.output.OutputBuilder

object GameStateUpdater {
  def updateState(gameState: GameState, events: List[Event]): GameState =
    events.foldLeft(gameState)((previousGameState, event) => applyEvent(previousGameState, event))

  private def applyEvent(previousGameState: GameState, event: Event): GameState =
    event match {
      case PlayerAdded(name) => GameState.addPlayer(previousGameState, name)
      case Prank(name, _, endSquare) => GameState.updatePlayerSquare(previousGameState, name, endSquare)
      case Stop(name, endSquare) => GameState.updatePlayerSquare(previousGameState, name, endSquare)
      case Win(_, _) => GameState.reset
      case _ => previousGameState
    }
}
