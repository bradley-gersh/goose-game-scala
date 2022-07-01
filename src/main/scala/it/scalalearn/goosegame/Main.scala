package it.scalalearn.goosegame

import scala.annotation.tailrec
import scala.io.StdIn.readLine
import scala.util.Random

object Main {
  private val ADD_PLAYER = """\s*(?i)add\s+player(?-i)\s+(\w+)""".r
  private val MOVE_PLAYER_RANDOM_DICE = """\s*(?i)move(?-i)\s+(\w+)""".r
  private val MOVE_PLAYER_CHOSEN_DICE = """\s*(?i)move(?-i)\s+(\w+)\s+(\d+),\s*(\d+)""".r
  private val random = new Random(System.nanoTime())
  private val banner: String = """
                                 |  Goose Game
                                 |
                                 |  To add player Pippo: "add player Pippo"
                                 |  To move player Pippo: "move Pippo"
                                 |  Enter blank line to exit.
                                 |
                                 |""".stripMargin

  def main(args: Array[String]): Unit = {
    println(banner)
    println(GameState())
    cli(GameState())
  }

  @tailrec
  def cli(gameState: GameState): Unit = {
    Option(readLine("> ")) match {
      case Some("") | None => println("goodbye\n")

      case Some(input) => 
        processInput(gameState, input) match {
          case Left(error) =>
            println(error + "\n")
            cli(gameState)

          case Right((newGameState, success)) =>
            println(success + "\n")
            cli(newGameState)
        }
    }
  }

  def processInput(gameState: GameState, input: String): Either[String, (GameState, String)] = {
    input match {
      case ADD_PLAYER(newName) => GameStateChanger.addPlayer(gameState, newName)

      case MOVE_PLAYER_CHOSEN_DICE(name, die1String, die2String) =>
        val dice = List(die1String.toInt, die2String.toInt)
        Logic.movePlayer(gameState, name, dice)

      case MOVE_PLAYER_RANDOM_DICE(name) =>
        val dice = List(random.nextInt(6) + 1, random.nextInt(6) + 1)
        Logic.movePlayer(gameState, name, dice)

      case "" => Left("no input")

      case _ => Left("unrecognized command")
    }
  }
}