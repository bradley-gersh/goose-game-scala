package it.scalalearn.goosegame.ui.output

sealed trait Output {
  def display(): Unit
}

case class ConsoleOutput(message: String) extends Output {
  def display(): Unit = println(message + "\n")
}