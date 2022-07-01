package it.scalalearn.goosegame.cli

import it.scalalearn.goosegame.cli.CLIStrings.{ADD_PLAYER_CMD, MOVE_PLAYER_CHOSEN_DICE_CMD, MOVE_PLAYER_RANDOM_DICE_CMD}
import it.scalalearn.goosegame.errors.{GameError, NoInputError, UnknownInputError}
import it.scalalearn.goosegame.gamestate.{GameState, GameStateChanger}
import it.scalalearn.goosegame.Logic
import it.scalalearn.goosegame.readout.Readout

import scala.annotation.tailrec
import scala.io.StdIn.readLine
import scala.util.Random

object CommandLineInterface {
  private val random = new Random(System.nanoTime())

  @tailrec
  def cli(gameState: GameState): Unit = {
    Option(readLine("> ")) match {
      case Some("") | None => println("goodbye\n")

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

  def processInput(gameState: GameState, input: String): Either[GameError, (GameState, Readout)] = {
    input match {
      case ADD_PLAYER_CMD(newName) => GameStateChanger.addPlayer(gameState, newName)

      case MOVE_PLAYER_CHOSEN_DICE_CMD(name, die1String, die2String) =>
        val dice = List(die1String.toInt, die2String.toInt)
        Logic.movePlayer(gameState, name, dice)

      case MOVE_PLAYER_RANDOM_DICE_CMD(name) =>
        val dice = List(random.nextInt(6) + 1, random.nextInt(6) + 1)
        Logic.movePlayer(gameState, name, dice)

      case "" => Left(NoInputError)

      case _ => Left(UnknownInputError)
    }
  }
}
