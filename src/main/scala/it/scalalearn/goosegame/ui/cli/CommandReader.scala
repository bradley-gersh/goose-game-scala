package it.scalalearn.goosegame.ui.cli

import it.scalalearn.goosegame.ui.errors.{GameError, NoInputError, UnknownInputError}
import it.scalalearn.goosegame.ui.cli.CliStrings.{AddPlayerCmd, EmptyCmd, MovePlayerChosenDiceCmd, MovePlayerRandomDiceCmd}

import scala.util.Random

object CommandReader {
  private val random = new Random(System.nanoTime())

  def interpret(input: String): Either[GameError, Command] = input match {
    case AddPlayerCmd(name) => Right(AddPlayer(name))

    case MovePlayerChosenDiceCmd(name, die1String, die2String) =>
      val dice = List(die1String.toInt, die2String.toInt)
      Right(MovePlayer(name, dice))

    case MovePlayerRandomDiceCmd(name) =>
      val dice = List(random.nextInt(6) + 1, random.nextInt(6) + 1)
      Right(MovePlayer(name, dice))

    case EmptyCmd() => Left(NoInputError)

    case _ => Left(UnknownInputError)
  }
}
