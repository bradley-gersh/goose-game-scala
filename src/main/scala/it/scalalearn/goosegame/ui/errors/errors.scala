package it.scalalearn.goosegame.ui.errors

sealed trait GameError(errorMsg: String) {
  def message: String = errorMsg
  def display(): Unit = println(errorMsg + "\n")
}

case object DiceError extends GameError(ErrorMessages.DICE_ERROR_MSG)
case object NoInputError extends GameError(ErrorMessages.NO_INPUT_ERROR_MSG)
case object UnknownInputError extends GameError(ErrorMessages.UNKNOWN_INPUT_ERROR_MSG)

case class DoubledPlayerError(name: String) extends GameError(ErrorMessages.DOUBLED_PLAYER_ERROR_MSG(name))
case class UnknownPlayerError(name: String) extends GameError(ErrorMessages.UNKNOWN_PLAYER_ERROR_MSG(name))
