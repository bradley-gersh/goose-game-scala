package it.scalalearn.goosegame

import it.scalalearn.goosegame.internal.gamestate.GameState
import it.scalalearn.goosegame.ui.cli.CLIStrings.START_MSG
import it.scalalearn.goosegame.ui.cli.CommandLineInterface.cli

object Main {
  def main(args: Array[String]): Unit = {
    println(START_MSG)
    cli(GameState())
  }
}