package it.scalalearn.goosegame.ui.output

case class FinalOutput(message: String) {
  def display(): Unit = println(message + "\n")
}

case class OutputData(messages: List[String]) {
  def seal(): FinalOutput = FinalOutput(messages.reverse.mkString(""))
}
