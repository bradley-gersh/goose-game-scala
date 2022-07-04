package it.scalalearn.goosegame.movelogic

import it.scalalearn.goosegame.errors.{DiceError, DoubledPlayerError, GameError, UnknownPlayerError}
import it.scalalearn.goosegame.gamestate.GameState
import it.scalalearn.goosegame.gamestate.SpecialSquares.{BRIDGE, BRIDGE_END, GOOSE_SQUARES, LAST_SQUARE}
import it.scalalearn.goosegame.rosterlogic.RosterHandler
import it.scalalearn.goosegame.readout.{FinalReadout, IntermediateReadout, ReadoutBuilder}

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
  def advance(gameState: GameState, move: Move, intermediateReadout: IntermediateReadout): (GameState, IntermediateReadout) = {
    val Move(name, previousSquare, startSquare, dice) = move
    assert(validateDice(dice).isRight)
    assert(gameState.hasPlayer(name))

    // don't return immediately from this match. Make a new variable, like nextSquare, to pull out of the match.
    // Then do and return something AFTER the match statement. Consider also having an object that builds the
    // output status. Here it's just a message, but it might be something else in the future.
    previousSquare + dice.sum match {
      case LAST_SQUARE =>
        (GameState(), ReadoutBuilder.appendWin(intermediateReadout, name))

      case BRIDGE =>
        val updatedReadout = ReadoutBuilder.appendBridge(intermediateReadout, name)
        val (gameStateAfterPrank, prankMsg) = checkPrank(gameState, name, BRIDGE_END, startSquare)
        (GameState(gameStateAfterPrank, name, BRIDGE_END), ReadoutBuilder.appendPrank(updatedReadout, prankMsg))

      case newSquare if newSquare > LAST_SQUARE =>
        val bounceToSquare = LAST_SQUARE - (newSquare - LAST_SQUARE)
        val updatedReadout = ReadoutBuilder.appendBounce(intermediateReadout, name, bounceToSquare)
        val (gameStateAfterPrank, statusAfterPrank) = checkPrank(gameState, name, bounceToSquare, startSquare)
        (GameState(gameStateAfterPrank, name, bounceToSquare), ReadoutBuilder.appendPrank(updatedReadout, statusAfterPrank))

      case newSquare if GOOSE_SQUARES(newSquare) =>
        val updatedReadout = ReadoutBuilder.appendGooseStart(intermediateReadout, newSquare)
        val (gameStateAfterPrank, prankMsg) = checkPrank(gameState, name, newSquare, startSquare)
        val nextUpdatedReadout = ReadoutBuilder.appendPrank(updatedReadout, prankMsg)
        val lastUpdatedReadout = ReadoutBuilder.appendGooseContinue(nextUpdatedReadout, name)

        val nextMove = Move(name, newSquare, startSquare, dice)

        advance(GameState(gameStateAfterPrank, name, newSquare + dice.sum), nextMove, lastUpdatedReadout)

      case newSquare =>
        val updatedReadout = ReadoutBuilder.appendNormal(intermediateReadout, newSquare)
        val (gameStateAfterPrank, prankMsg) = checkPrank(gameState, name, newSquare, startSquare)
        (GameState(gameStateAfterPrank, name, newSquare), ReadoutBuilder.appendPrank(updatedReadout, prankMsg))
    }
  }

  def checkPrank(gameState: GameState, name: String, newSquare: Int, startSquare: Int): (GameState, String) = {
    val bumpNames = gameState.playersOnSquare(name, newSquare)

    bumpNames.foldLeft((gameState, ""))(
      (output, player) => {
        val (newGameState, status) = output

        (GameState(newGameState, player, startSquare),
          status + s". On $newSquare there is $player, who returns to $startSquare")
      }
    )
  }

  def validateDice(dice: List[Int]): Either[GameError, List[Int]] =
    if (dice.forall(die => die >= 1 && die <= 6))
      Right(dice)
    else
      Left(DiceError)
}
