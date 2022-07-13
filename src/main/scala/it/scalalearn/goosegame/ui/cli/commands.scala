package it.scalalearn.goosegame.ui.cli

import it.scalalearn.goosegame.internal.events.Dice

sealed trait Command

case class AddPlayer(name: String) extends Command
case class MovePlayer(name: String, dice: Dice) extends Command
case object Quit extends Command