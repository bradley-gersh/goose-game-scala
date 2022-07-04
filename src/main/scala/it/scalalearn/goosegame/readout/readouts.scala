package it.scalalearn.goosegame.readout

case class FinalReadout(message: String) {
  def display(): Unit = println(message + "\n")
}

case class IntermediateReadout(messages: List[String]) {
  def seal(): FinalReadout = FinalReadout(messages.reverse.mkString(""))
}
