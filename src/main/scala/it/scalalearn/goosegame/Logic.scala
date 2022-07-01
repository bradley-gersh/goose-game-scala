package it.scalalearn.goosegame

import scala.annotation.tailrec
import scala.collection.mutable

import Constants._

object Logic {
  def movePlayer(gameState: GameState, name: String, dice: List[Int]): Either[String, (GameState, String)] = {
    for {
      validatedDice <- validateDice(dice)
      oldSquare <- gameState.getPlayerSquare(name).toRight(s"$name: unrecognized player")
    } yield {
      val status = new mutable.StringBuilder(s"$name rolls ${validatedDice.mkString(", ")}. $name moves from ${if (oldSquare == 0) "Start" else oldSquare} to ")
      val (newGameState, newStatus) = advance(gameState, name, oldSquare, oldSquare, dice, status) // consider wrapping parameters into an object

      (newGameState, newStatus.toString)
    }
  }

  @tailrec
  def advance(gameState: GameState, name: String, oldSquare: Int, startSquare: Int, dice: List[Int], status: mutable.StringBuilder): (GameState, mutable.StringBuilder) = {
    assert(validateDice(dice).isRight)
    assert(gameState.hasPlayer(name))

    // don't return immediately from this match. Make a new variable, like nextSquare, to pull out of the match.
    // Then do and return something AFTER the match statement. Consider also having an object that builds the
    // output status. Here it's just a message, but it might be something else in the future.
    oldSquare + dice.sum match {
      case LAST_SQUARE => (GameState(), status.append(s"$LAST_SQUARE. $name Wins!!"))

      case BRIDGE =>
        val (gameStateAfterPrank, statusAfterPrank) = checkPrank(gameState, name, BRIDGE_END, startSquare)
        (GameStateChanger.move(gameStateAfterPrank, name, BRIDGE_END), status.append(s"The Bridge. $name jumps to $BRIDGE_END").append(statusAfterPrank))

      case newSquare if newSquare > LAST_SQUARE =>
        val bounceTo = LAST_SQUARE - (newSquare - LAST_SQUARE)
        val (gameStateAfterPrank, statusAfterPrank) = checkPrank(gameState, name, bounceTo, startSquare)
        (GameStateChanger.move(gameStateAfterPrank, name, bounceTo), status.append(s"$LAST_SQUARE. $name bounces! $name returns to $bounceTo").append(statusAfterPrank))

      case newSquare if GOOSE_SQUARES(newSquare) =>
        val (gameStateAfterPrank, statusAfterPrank) = checkPrank(gameState, name, newSquare, startSquare)
        advance(GameStateChanger.move(gameStateAfterPrank, name, newSquare + dice.sum),
          name,
          newSquare,
          startSquare,
          dice,
          status.append(s"$newSquare, The Goose").append(statusAfterPrank).append(s". $name moves again and goes to "))

      case newSquare =>
        val (gameStateAfterPrank, statusAfterPrank) = checkPrank(gameState, name, newSquare, startSquare)
        (GameStateChanger.move(gameStateAfterPrank, name, newSquare), status.append(s"$newSquare").append(statusAfterPrank))
    }
  }

  def checkPrank(gameState: GameState, name: String, newSquare: Int, startSquare: Int): (GameState, mutable.StringBuilder) = {
    val bumpNames = gameState.playersOnSquare(name, newSquare)
    bumpNames.foldLeft((gameState, new mutable.StringBuilder()))(
      (output, player) => {
        val (newGameState, status) = output

        (GameStateChanger.move(newGameState, player, startSquare),
          status.append(s". On $newSquare there is $player, who returns to $startSquare"))
      }
    )
  }

  def validateDice(dice: List[Int]): Either[String, List[Int]] =
    if (dice.forall(die => die >= 1 && die <= 6)) Right(dice) else Left("Dice must have value from 1 to 6")
}
