package it.scalalearn.goosegame

import scala.annotation.tailrec
import scala.collection.mutable

import it.scalalearn.goosegame.errors._
import it.scalalearn.goosegame.gamestate._
import it.scalalearn.goosegame.gamestate.SpecialSquares._
import it.scalalearn.goosegame.readout._

object Logic {
  def movePlayer(gameState: GameState, name: String, dice: List[Int]): Either[GameError, (GameState, Readout)] = {
    for {
      _ <- validateDice(dice)
      oldSquare <- validatePlayer(gameState, name)
    } yield {
      val status = new mutable.StringBuilder(
        s"$name rolls ${dice.mkString(", ")}. $name moves from ${if (oldSquare == 0) "Start" else oldSquare} to ")
      val (newGameState, newStatus) = advance(gameState, name, oldSquare, oldSquare, dice, status) // consider wrapping parameters into an object

      (newGameState, Readout(newStatus.toString))
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
        (GameStateChanger.movePlayer(gameStateAfterPrank, name, BRIDGE_END), status.append(s"The Bridge. $name jumps to $BRIDGE_END").append(statusAfterPrank))

      case newSquare if newSquare > LAST_SQUARE =>
        val bounceTo = LAST_SQUARE - (newSquare - LAST_SQUARE)
        val (gameStateAfterPrank, statusAfterPrank) = checkPrank(gameState, name, bounceTo, startSquare)
        (GameStateChanger.movePlayer(gameStateAfterPrank, name, bounceTo), status.append(s"$LAST_SQUARE. $name bounces! $name returns to $bounceTo").append(statusAfterPrank))

      case newSquare if GOOSE_SQUARES(newSquare) =>
        val (gameStateAfterPrank, statusAfterPrank) = checkPrank(gameState, name, newSquare, startSquare)
        advance(GameStateChanger.movePlayer(gameStateAfterPrank, name, newSquare + dice.sum),
          name,
          newSquare,
          startSquare,
          dice,
          status.append(s"$newSquare, The Goose").append(statusAfterPrank).append(s". $name moves again and goes to "))

      case newSquare =>
        val (gameStateAfterPrank, statusAfterPrank) = checkPrank(gameState, name, newSquare, startSquare)
        (GameStateChanger.movePlayer(gameStateAfterPrank, name, newSquare), status.append(s"$newSquare").append(statusAfterPrank))
    }
  }

  def checkPrank(gameState: GameState, name: String, newSquare: Int, startSquare: Int): (GameState, mutable.StringBuilder) = {
    val bumpNames = gameState.playersOnSquare(name, newSquare)
    bumpNames.foldLeft((gameState, new mutable.StringBuilder()))(
      (output, player) => {
        val (newGameState, status) = output

        (GameStateChanger.movePlayer(newGameState, player, startSquare),
          status.append(s". On $newSquare there is $player, who returns to $startSquare"))
      }
    )
  }

  def validateDice(dice: List[Int]): Either[GameError, List[Int]] =
    if (dice.forall(die => die >= 1 && die <= 6)) Right(dice) else Left(DiceError)

  def validatePlayer(gameState: GameState, name: String): Either[GameError, Int] =
    gameState.getPlayerSquare(name).toRight(UnknownPlayerError(name))
}
