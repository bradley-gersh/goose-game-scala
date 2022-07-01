package it.scalalearn.goosegame.cli

import scala.util.matching.Regex

object CLIStrings {
  final val START_MSG: String = """
                       |  Goose Game
                       |
                       |  To add player Pippo: "add player Pippo"
                       |  To move player Pippo: "move Pippo"
                       |  Enter blank line to exit.
                       |
                       |""".stripMargin
  final val PROMPT: String = "> "
  final val EXIT_MSG: String = "goodbye\n"

  final val ADD_PLAYER_CMD: Regex = """\s*(?i)add\s+player(?-i)\s+(\w+)""".r
  final val MOVE_PLAYER_RANDOM_DICE_CMD: Regex = """\s*(?i)move(?-i)\s+(\w+)""".r
  final val MOVE_PLAYER_CHOSEN_DICE_CMD: Regex = """\s*(?i)move(?-i)\s+(\w+)\s+(\d+),\s*(\d+)""".r
}
