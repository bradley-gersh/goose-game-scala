package it.scalalearn.goosegame

import scala.io.StdIn.readLine
import scala.util.Random
object Main {
  def main(args: Array[String]): Unit = {
    println(banner)
    cli(Map[String, Int]())
  }

  val banner = """
    |  Goose Game
    |
    |  To add player Pippo: "add player Pippo"
    |  To move player Pippo: "move Pippo"
    |  Enter blank line to exit.
    |
    |""".stripMargin

  def cli(oldGameState: Map[String, Int]): Unit = {
    Option(readLine("> ")) match {
      case Some("") | None => println("goodbye\n")
      case Some(input) => 
        processInput(input, oldGameState) match {
          case Left(error) =>
            println(error + "\n")
            cli(oldGameState)
          case Right(newGameState, success) =>
            println(success + "\n")
            cli(newGameState)
        }
    }
  }

  def processInput(input: String, gameState: Map[String, Int]): Either[String, (Map[String, Int], String)] = {
    if (input.isEmpty) {
      Left("no input")
    } else input.split(" ") match {
        case Array("add", "player", newName) => Logic.addPlayer(gameState, newName)
        case Array("move", name, die1String, die2String) => {
          val die1 = if (die1String.last == ',')
              die1String.substring(0, die1String.length() - 1).toInt
            else die1String.toInt
          val die2 = die2String.toInt
          
          Logic.movePlayer(gameState, name, die1, die2)
        }
        case Array("move", name) => {
          val random = Random(System.nanoTime())
          val die1 = random.nextInt(6) + 1
          val die2 = random.nextInt(6) + 1
          
          Logic.movePlayer(gameState, name, die1, die2)
        }
        case _ => Left("unrecognized command")
      }
  }
}