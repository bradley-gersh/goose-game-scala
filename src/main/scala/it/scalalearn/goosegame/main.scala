package it.scalalearn.goosegame

import scala.io.StdIn.readLine

object Main {
    def main(args: Array[String]): Unit = {
        println(banner)
        cli(GameState())
    }

    val banner = """
        |  Goose Game
        |
        |  To add player Pippo: "add player Pippo"
        |  To move player Pippo: "move Pippo"
        |  Enter blank line to exit.
        |
        |""".stripMargin

    def cli(gameState: GameState): Unit = {
        Option(readLine("> ")) match {
            case Some("") | None => println("goodbye\n")
            case Some(input) => val newGameState = processInput(input, gameState)
                println(newGameState.status + "\n")
                cli(newGameState)
        }
    }

    def processInput(input: String, gameState: GameState): GameState = {
        if (input.isEmpty) {
            gameState.setStatus("no input")
        } else input.split(" ") match {
                case Array("add", "player", newName) => {
                    gameState.addPlayer(newName)
                }
                case _ => gameState.setStatus("unrecognized command")
            }
    }
}