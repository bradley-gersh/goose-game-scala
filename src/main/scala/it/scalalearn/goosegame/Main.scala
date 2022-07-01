package it.scalalearn.goosegame

import it.scalalearn.goosegame.cli.CLIStrings.START_MSG
import it.scalalearn.goosegame.cli.CommandLineInterface.cli
import it.scalalearn.goosegame.gamestate.GameState

object Main {
  def main(args: Array[String]): Unit = {
    println(START_MSG)
    println(GameState())
    cli(GameState())
  }
}