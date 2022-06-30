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
    cli(Map[String, Int]())
  }

  @tailrec
  def cli(oldGameState: Map[String, Int]): Unit = {
    Option(readLine("> ")) match {
      case Some("") | None => println("goodbye\n")
      case Some(input) => 
        processInput(input, oldGameState) match {
          case Left(error) =>
            println(error + "\n")
            cli(oldGameState)
          case Right((newGameState, success)) =>
            println(success + "\n")
            cli(newGameState)
        }
    }
  }

  def processInput(input: String, gameState: Map[String, Int]): Either[String, (Map[String, Int], String)] = {
    if (input.isEmpty) {
      Left("no input")
    } else input match {
        case ADD_PLAYER(newName) => Logic.addPlayer(gameState, newName)

        case MOVE_PLAYER_CHOSEN_DICE(name, die1String, die2String) =>
          val (die1, die2) = (die1String.toInt, die2String.toInt)
          Logic.movePlayer(gameState, name, die1, die2)

        case MOVE_PLAYER_RANDOM_DICE(name) =>
          val (die1, die2) = (random.nextInt(6) + 1, random.nextInt(6) + 1)
          Logic.movePlayer(gameState, name, die1, die2)

        case _ => Left("unrecognized command")
      }
  }
}