package it.scalalearn.goosegame.ui.output

case class Output(message: String) {
  def display(): Unit = println(message + "\n")
}