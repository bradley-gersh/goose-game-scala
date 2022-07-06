package it.scalalearn.goosegame.ui.cli

import it.scalalearn.goosegame.internal.gamestate.{GameState, GameStateUpdater}
import it.scalalearn.goosegame.internal.events.{MoveScriptWriter, RosterScriptWriter, ScriptWriter}
import it.scalalearn.goosegame.ui.cli.CLIStrings.{EXIT_MSG, PROMPT}
import it.scalalearn.goosegame.ui.errors.GameError
import it.scalalearn.goosegame.ui.output.{Output, OutputBuilder}

import scala.annotation.tailrec
import scala.io.StdIn.readLine
import scala.util.Random

object CommandLineInterface {
  @tailrec
  def cli(gameState: GameState): Unit = {
    Option(readLine(PROMPT)) match {
      case Some("") | None => println(EXIT_MSG)

      case Some(input) =>
        processInput(gameState, input) match {
          case Left(error) =>
            error.display()
            cli(gameState)

          case Right((newGameState, successOutput)) =>
            successOutput.display()
            cli(newGameState)
        }
    }
  }

  def processInput(gameState: GameState, input: String): Either[GameError, (GameState, Output)] = {
    for {
      command <- CommandReader.interpret(input)
      events <- ScriptWriter.writeEvents(gameState, command)
      newGameState <- GameStateUpdater.updateState(gameState, events)
    } yield {
      println(events.mkString("[", ", ", "]"))
      (newGameState, OutputBuilder.transcribe(newGameState, events))
    }
  }
}
