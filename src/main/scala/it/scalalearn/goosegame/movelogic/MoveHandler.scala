package it.scalalearn.goosegame.movelogic

import it.scalalearn.goosegame.errors.{DiceError, DoubledPlayerError, GameError, UnknownPlayerError}
import it.scalalearn.goosegame.gamestate.GameState
import it.scalalearn.goosegame.gamestate.SpecialSquares.{BRIDGE, BRIDGE_END, GOOSE_SQUARES, LAST_SQUARE}
import it.scalalearn.goosegame.rosterlogic.RosterHandler
import it.scalalearn.goosegame.readout.{FinalReadout, ReadoutData, ReadoutBuilder}

import scala.annotation.tailrec
import scala.collection.mutable

object MoveHandler {
  def movePlayer(gameState: GameState, name: String, dice: List[Int]): Either[GameError, (GameState, FinalReadout)] = {
    for {
      validDice <- validateDice(dice)
      previousSquare <- gameState.getPlayerSquare(name)
    } yield {
      val move = Move(name, previousSquare, previousSquare, validDice)
      val startReadout = ReadoutBuilder.logStartRoll(name, previousSquare, validDice)

      val (newGameState, finalReadout) = advance(gameState, move, startReadout)

      (newGameState, finalReadout.seal())
    }
  }

  @tailrec
  def advance(gameState: GameState, move: Move, ReadoutData: ReadoutData): (GameState, ReadoutData) = {
    val Move(name, previousSquare, startSquare, dice) = move

    assert(validateDice(dice).isRight)
    assert(gameState.hasPlayer(name))

    val square = previousSquare + dice.sum

    // don't return immediately from this match. Make a new variable, like nextSquare, to pull out of the match.
    // Then do and return something AFTER the match statement. Consider also having an object that builds the
    // output status. Here it's just a message, but it might be something else in the future.

    square match {
      case LAST_SQUARE =>
        (GameState(), ReadoutBuilder.appendWin(ReadoutData, name))

      case BRIDGE =>
        val updatedReadout = ReadoutBuilder.appendBridge(ReadoutData, name)
        val (gameStateAfterPrank, prankedReadout) = checkPrank(gameState, name, BRIDGE_END, startSquare, updatedReadout)
        (GameState(gameStateAfterPrank, name, BRIDGE_END), prankedReadout)

      case newSquare if newSquare > LAST_SQUARE =>
        val bounceToSquare = LAST_SQUARE - (newSquare - LAST_SQUARE)
        val updatedReadout = ReadoutBuilder.appendBounce(ReadoutData, name, bounceToSquare)
        val (gameStateAfterPrank, prankedReadout) = checkPrank(gameState, name, bounceToSquare, startSquare, updatedReadout)
        (GameState(gameStateAfterPrank, name, bounceToSquare), prankedReadout)

      case newSquare if GOOSE_SQUARES(newSquare) =>
        val updatedReadout = ReadoutBuilder.appendGooseStart(ReadoutData, newSquare)
        val (gameStateAfterPrank, prankedReadout) = checkPrank(gameState, name, newSquare, startSquare, updatedReadout)
        val lastUpdatedReadout = ReadoutBuilder.appendGooseContinue(prankedReadout, name)

        val nextMove = Move(name, newSquare, startSquare, dice)

        advance(GameState(gameStateAfterPrank, name, newSquare + dice.sum), nextMove, lastUpdatedReadout)

      case newSquare =>
        val updatedReadout = ReadoutBuilder.appendNormal(ReadoutData, newSquare)
        val (gameStateAfterPrank, prankedReadout) = checkPrank(gameState, name, newSquare, startSquare, updatedReadout)
        (GameState(gameStateAfterPrank, name, newSquare), prankedReadout)
    }
  }

  def checkPrank(gameState: GameState,
                 name: String,
                 square: Int,
                 startSquare: Int,
                 ReadoutData: ReadoutData): (GameState, ReadoutData) = {
    val bumpNames = gameState.playersOnSquare(name, square)

    bumpNames.foldLeft((gameState, ReadoutData))(
      (output, otherPlayer) => {
        val (newGameState, currentReadout) = output

        (GameState(newGameState, otherPlayer, startSquare),
          ReadoutBuilder.appendPrank(currentReadout, otherPlayer, square, startSquare))
      }
    )
  }

  def validateDice(dice: List[Int]): Either[GameError, List[Int]] =
    if (dice.forall(die => die >= 1 && die <= 6))
      Right(dice)
    else
      Left(DiceError)
}
