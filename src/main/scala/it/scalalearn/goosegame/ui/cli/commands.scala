package it.scalalearn.goosegame.ui.cli

sealed trait Command

case class AddPlayer(name: String) extends Command
case class MovePlayer(name: String, dice: List[Int]) extends Command
case object Quit extends Command