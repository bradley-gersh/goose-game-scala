package it.scalalearn.goosegame

import it.scalalearn.goosegame.internal.gamestate.GameState
import it.scalalearn.goosegame.ui.cli.CliStrings.StartMsg
import it.scalalearn.goosegame.ui.cli.CommandLineInterface.cli

object Main extends App {
  println(StartMsg)
  cli(GameState())
}