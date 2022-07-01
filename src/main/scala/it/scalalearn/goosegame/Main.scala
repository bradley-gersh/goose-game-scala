package it.scalalearn.goosegame

import it.scalalearn.goosegame.cli.CLIStrings.banner
import it.scalalearn.goosegame.cli.CommandLineInterface.cli
import it.scalalearn.goosegame.gamestate.GameState

object Main {
  def main(args: Array[String]): Unit = {
    println(banner)
    println(GameState())
    cli(GameState())
  }
}