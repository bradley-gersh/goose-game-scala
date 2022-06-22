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
    val gameState = GameState("Pluto", "Pippo")
    val inputs = List("move Pippo 4, 2", "move Pluto 2, 2", "move Pippo 2, 3")
    val refOutputs = List(
      "Pippo rolls 4, 2. Pippo moves from Start to 6",
      "Pluto rolls 2, 2. Pluto moves from Start to 4",
      "Pippo rolls 2, 3. Pippo moves from 6 to 11"
    )
    assert(getScript(inputs, gameState) == refOutputs)
  }

  test("Player wins if (s)he lands on square 63") {
    val gameState = GameState(Map(("Pippo" -> 60)))
    val inputs = List("move Pippo 1, 2")
    val refOutputs = List("Pippo rolls 1, 2. Pippo moves from 60 to 63. Pippo Wins!!")
    assert(getScript(inputs, gameState) == refOutputs)
  }

  test("Player bounces if the rolled square exceeds 63") {
    val gameState = GameState(Map(("Pippo" -> 60)))
    val inputs = List("move Pippo 3, 2")
    val refOutputs = List("Pippo rolls 3, 2. Pippo moves from 60 to 63. Pippo bounces! Pippo returns to 61")
  }

  // test("Dice cannot be outside range 1-6")
  // test("Cannot add player once the game has started")
  // test("Players must take turns in correct sequence")
}