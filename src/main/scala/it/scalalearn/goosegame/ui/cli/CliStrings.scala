package it.scalalearn.goosegame.ui.cli

import scala.util.matching.Regex

object CliStrings {
  final val StartMsg: String = """
                       |  Goose Game
                       |
                       |  To add player Pippo: "add player Pippo"
                       |  To move player Pippo: "move Pippo"
                       |  Enter blank line to exit.
                       |
                       |""".stripMargin
  final val Prompt: String = "> "
  final val ExitMsg: String = "goodbye\n"

  final val AddPlayerCmd: Regex = """\s*(?i)add\s+player(?-i)\s+(\w+)""".r
  final val MovePlayerRandomDiceCmd: Regex = """\s*(?i)move(?-i)\s+(\w+)""".r
  final val MovePlayerChosenDiceCmd: Regex = """\s*(?i)move(?-i)\s+(\w+)\s+(\d+),\s*(\d+)""".r
}
