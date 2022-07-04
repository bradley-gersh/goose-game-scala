package it.scalalearn.goosegame.ui.cli

import it.scalalearn.goosegame.internal.gamestate.GameState
import it.scalalearn.goosegame.internal.movelogic.{RawMove, MoveHandler}
import it.scalalearn.goosegame.internal.rosterlogic.RosterHandler
import it.scalalearn.goosegame.ui.cli.CLIStrings.{ADD_PLAYER_CMD, EXIT_MSG, MOVE_PLAYER_CHOSEN_DICE_CMD,
  MOVE_PLAYER_RANDOM_DICE_CMD, PROMPT}
import it.scalalearn.goosegame.ui.errors.{GameError, NoInputError, UnknownInputError}
import it.scalalearn.goosegame.ui.readout.FinalReadout

import scala.annotation.tailrec
import scala.io.StdIn.readLine
import scala.util.Random

object CommandLineInterface {
  private val random = new Random(System.nanoTime())

  @tailrec
  def cli(gameState: GameState): Unit = {
    Option(readLine(PROMPT)) match {
      case Some("") | None => println(EXIT_MSG)

      case Some(input) =>
        processInput(gameState, input) match {
          case Left(error) =>
            error.display()
            cli(gameState)

          case Right((newGameState, successReadout)) =>
            successReadout.display()
            cli(newGameState)
        }
    }
  }

  def processInput(gameState: GameState, input: String): Either[GameError, (GameState, FinalReadout)] = {
    input match {
      case ADD_PLAYER_CMD(newName) => RosterHandler.addPlayer(gameState, newName)

      case MOVE_PLAYER_CHOSEN_DICE_CMD(name, die1String, die2String) =>
        val dice = List(die1String.toInt, die2String.toInt)
        MoveHandler.movePlayer(gameState, name, dice)

      case MOVE_PLAYER_RANDOM_DICE_CMD(name) =>
        val dice = List(random.nextInt(6) + 1, random.nextInt(6) + 1)
        MoveHandler.movePlayer(gameState, name, dice)

      case "" => Left(NoInputError)

      case _ => Left(UnknownInputError)
    }
  }
}
