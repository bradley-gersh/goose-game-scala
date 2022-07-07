package it.scalalearn.goosegame.ui.cli

import it.scalalearn.goosegame.internal.gamestate.{GameState, GameStateUpdater}
import it.scalalearn.goosegame.internal.events.{Event, MoveScriptWriter, QuitEvent, RosterScriptWriter, ScriptWriter}
import it.scalalearn.goosegame.ui.cli.CliStrings.Prompt
import it.scalalearn.goosegame.ui.errors.GameError
import it.scalalearn.goosegame.ui.output.{Output, OutputBuilder}

import scala.annotation.tailrec
import scala.io.StdIn.readLine

object CommandLineInterface {
  @tailrec
  def cli(gameState: GameState): Unit = {
    val input = Option(readLine(Prompt)).getOrElse("")
    processInput(gameState, input) match {
      case Left(error) =>
        error.display()
        cli(gameState)

      case Right((newGameState, events, successOutput)) =>
        successOutput.display()
        if (hasQuitEvent(events))
          quit()
        else
          cli(newGameState)
    }
  }

  def processInput(gameState: GameState, input: String): Either[GameError, (GameState, List[Event], Output)] = {
    for {
      command <- CommandReader.interpret(input)
      events <- ScriptWriter.writeEvents(gameState, command)
      newGameState <- GameStateUpdater.updateState(gameState, events)
    } yield (newGameState, events, OutputBuilder.transcribe(newGameState, events))
  }

  def hasQuitEvent(events: List[Event]): Boolean = events.contains(QuitEvent)

  def quit(): Unit = () // cleanup routines could happen here
}
