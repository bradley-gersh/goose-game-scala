package it.scalalearn.goosegame.ui.cli

import it.scalalearn.goosegame.internal.events.Dice
import it.scalalearn.goosegame.ui.errors.{DiceError, GameError, NoInputError, UnknownInputError}
import it.scalalearn.goosegame.ui.cli.CliStrings.{AddPlayerCmd, EmptyCmd, MovePlayerChosenDiceCmd, MovePlayerRandomDiceCmd, QuitCmd}

import scala.util.Random

object CommandReader {
  private val random = new Random(System.nanoTime())

  def interpret(input: String): Either[GameError, Command] = input match {
    case AddPlayerCmd(name) => Right(AddPlayer(name))

    case MovePlayerChosenDiceCmd(name, diceStrings*) =>
      Dice.makeDice(diceStrings.map(_.toInt)*).map(dice => MovePlayer(name, dice))

    case MovePlayerRandomDiceCmd(name) =>
      Dice.makeDice(random.nextInt(6) + 1, random.nextInt(6) + 1).map(dice => MovePlayer(name, dice))

    case QuitCmd(_) => Right(Quit)

    case EmptyCmd() => Left(NoInputError)

    case _ => Left(UnknownInputError)
  }
}
