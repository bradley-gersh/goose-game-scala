package it.scalalearn.goosegame.errors

import it.scalalearn.goosegame.errors.ErrorStrings._

sealed trait GameError(errorMsg: String) {
  def message: String = errorMsg
  def display(): Unit = println(errorMsg + "\n")
}

case object DiceError extends GameError(DICE_ERROR_MSG)
case object NoInputError extends GameError(NO_INPUT_ERROR_MSG)
case object UnknownInputError extends GameError(UNKNOWN_INPUT_ERROR_MSG)

case class DoubledPlayerError(name: String) extends GameError(DOUBLED_PLAYER_ERROR_MSG(name))
case class UnknownPlayerError(name: String) extends GameError(UNKNOWN_PLAYER_ERROR_MSG(name))
