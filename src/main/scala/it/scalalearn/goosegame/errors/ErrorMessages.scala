package it.scalalearn.goosegame.errors

object ErrorMessages {
  final val DICE_ERROR_MSG = "Dice must have value from 1 to 6"
  final val NO_INPUT_ERROR_MSG = "no input"
  final val UNKNOWN_INPUT_ERROR_MSG = "unrecognized command"

  final def DOUBLED_PLAYER_ERROR_MSG(name: String) = s"$name: already existing player"
  final def UNKNOWN_PLAYER_ERROR_MSG(name: String) = s"$name: unrecognized player"
}