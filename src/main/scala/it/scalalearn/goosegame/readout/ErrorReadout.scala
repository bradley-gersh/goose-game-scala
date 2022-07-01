package it.scalalearn.goosegame.readout

case class ErrorReadout(message: String) {
  def display(): Unit = println(message + "\n")
}
