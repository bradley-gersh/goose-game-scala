package it.scalalearn.goosegame

import scala.collection.mutable.ListBuffer
import org.scalatest.funsuite.AnyFunSuite
import java.io.{ByteArrayOutputStream, StringReader}

class MainTest extends AnyFunSuite {
  // Integration tests
  def getScript(inputs: List[String], startGameState: GameState): List[String] = {
    var transcript = ListBuffer[String]()
    val finalState = inputs.foldLeft(startGameState)((gameState, input) => {
      val updatedGameState = Main.processInput(input, gameState)
      transcript.append(updatedGameState.status)
      updatedGameState
    })
    transcript.toList
  }

  test("Adds a player to the game") {
    val gameState = GameState()
    val inputs = List("add player Pippo")
    val refOutputs = List("players: Pippo")
    assert(getScript(inputs, gameState) == refOutputs)
  }

  test("Duplicate players are not allowed") {
    val gameState = GameState()
    val inputs = List("add player Pippo", "add player Pluto")
    val refOutputs = List("players: Pippo", "players: Pippo, Pluto")
    assert(getScript(inputs, gameState) == refOutputs)
  }

  test("Players can move by specifying dice") {
    val gameState = GameState("Pippo", "Pluto")
    val inputs = List("move Pippo 4, 3", "move Pluto 2, 2", "move Pippo 2, 3")
    val refOutputs = List(
      "Pippo rolls 4, 3. Pippo moves from Start to 7",
      "Pluto rolls 2, 2. Pluto moves from Start to 4",
      "Pippo rolls 2, 3. Pippo moves from 7 to 12"
    )
    assert(getScript(inputs, gameState) == refOutputs)
  }

  test("Dice cannot be specified outside range 1-6") {
    val gameState = GameState("Pippo")
    val inputs = List("move Pippo 4, 8")
    val List(output) = getScript(inputs, gameState)
    assert(output.contains("dice must be"))
  }

  test("Player wins if he or she lands on square 63") {
    val gameState = GameState(Map(("Pippo" -> 60)))
    val inputs = List("move Pippo 1, 2")
    val refOutputs = List("Pippo rolls 1, 2. Pippo moves from 60 to 63. Pippo Wins!!")
    assert(getScript(inputs, gameState) == refOutputs)
  }

  test("Player bounces if the rolled square exceeds 63") {
    val gameState = GameState(Map(("Pippo" -> 60)))
    val inputs = List("move Pippo 3, 2")
    val refOutputs = List("Pippo rolls 3, 2. Pippo moves from 60 to 63. Pippo bounces! Pippo returns to 61")
    assert(getScript(inputs, gameState) == refOutputs)
  }

  test("Game rolls dice automatically") {
    val gameState = GameState(Map(("Pippo" -> 4)))
    val inputs = List("move Pippo")
    val List(output) = getScript(inputs, gameState)
    assert(output.contains("Pippo rolls"))
    assert(output.contains("Pippo moves from"))
  }

  test("When player lands on space 6, he or she advances to space 12") {
    val gameState = GameState(Map(("Pippo" -> 4)))
    val inputs = List("move Pippo 1, 1")
    val refOutputs = List("Pippo rolls 1, 1. Pippo moves from 4 to The Bridge. Pippo jumps to 12")
    assert(getScript(inputs, gameState) == refOutputs)
  }

  test("Player doubles move if he or she lands on The Goose") {
    val gameState = GameState(Map(("Pippo" -> 3)))
    val inputs = List("move Pippo 1, 1")
    val refOutputs = List("Pippo rolls 1, 1. Pippo moves from 3 to 5, The Goose. Pippo moves again and goes to 7")
    assert(getScript(inputs, gameState) == refOutputs)
  }

  test("Squares 5, 9, 14, 18, 23, 27 are all Goose squares") {
    val die1 = 1
    val die2 = 3
    val sum = die1 + die2

    List(5, 9, 14, 18, 23, 27).foreach(goose => {
      val gameState = GameState(Map(("Pippo" -> (goose - sum))))
      val inputs = List(s"move Pippo $die1, $die2")
      val refOutputs = List(s"Pippo rolls $die1, $die2. Pippo moves from ${goose - sum} to $goose, The Goose. Pippo moves again and goes to ${goose + sum}")
      assert(getScript(inputs, gameState) == refOutputs)
    })
  }

  test("Player can do multiple jumps from a Goose square") {
    val gameState = GameState(Map(("Pippo" -> 10)))
    val inputs = List("move Pippo 2, 2")
    val refOutputs = List("Pippo rolls 2, 2. Pippo moves from 10 to 14, The Goose. Pippo moves again and goes to 18, The Goose. Pippo moves again and goes to 22")
    assert(getScript(inputs, gameState) == refOutputs)
  }

  test("When one player lands on another's square, the latter is sent back to the first player's previous square") {
    val gameState = GameState(Map(("Pippo" -> 15), ("Pluto" -> 17)))
    val inputs = List("move Pippo 1, 1")
    val refOutputs = List("Pippo rolls 1, 1. Pippo moves from 15 to 17. On 17 there is Pluto, who returns to 15")
    assert(getScript(inputs, gameState) == refOutputs)
  }
  // test("Cannot add player once the game has started")
  // test("Players must take turns in correct sequence")
}