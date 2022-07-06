package it.scalalearn.goosegame.ui.cli

import it.scalalearn.goosegame.ui.errors.{GameError, NoInputError, UnknownInputError}
import it.scalalearn.goosegame.ui.cli.CLIStrings.{ADD_PLAYER_CMD, MOVE_PLAYER_CHOSEN_DICE_CMD, MOVE_PLAYER_RANDOM_DICE_CMD}

import scala.util.Random

object CommandReader {
  private val random = new Random(System.nanoTime())

  def interpret(input: String): Either[GameError, Command] = input match {
    case ADD_PLAYER_CMD(name) => Right(AddPlayer(name))

    case MOVE_PLAYER_CHOSEN_DICE_CMD(name, die1String, die2String) =>
      val dice = List(die1String.toInt, die2String.toInt)
      Right(MovePlayer(name, dice))

    case MOVE_PLAYER_RANDOM_DICE_CMD(name) =>
      val dice = List(random.nextInt(6) + 1, random.nextInt(6) + 1)
      Right(MovePlayer(name, dice))

    case "" => Left(NoInputError)

    case _ => Left(UnknownInputError)
  }
}
