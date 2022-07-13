package it.scalalearn.goosegame.internal.events

import it.scalalearn.goosegame.ui.errors.{DiceError, GameError}

case class Dice(values: List[Int]) extends AnyVal {
  def sum: Int = values.sum
}

case object Dice {
  def makeDice(dice: Int*): Either[GameError, Dice] =
    if (dice.forall(die => die >= 1 && die <= 6))
      Right(Dice(dice.toList))
    else
      Left(DiceError)
}
