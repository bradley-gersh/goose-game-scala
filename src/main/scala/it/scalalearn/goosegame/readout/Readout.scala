package it.scalalearn.goosegame.readout

case class Readout(message: String) {
  def display(): Unit = println(message + "\n")
}