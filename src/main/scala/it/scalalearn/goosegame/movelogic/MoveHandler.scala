package it.scalalearn.goosegame.movelogic

import it.scalalearn.goosegame.errors.{DiceError, DoubledPlayerError, GameError, UnknownPlayerError}
import it.scalalearn.goosegame.gamestate.GameState
import it.scalalearn.goosegame.gamestate.SpecialSquares.{BRIDGE_SQUARE, BRIDGE_END, GOOSE_SQUARES, LAST_SQUARE}
import it.scalalearn.goosegame.movelogic.MoveType
import it.scalalearn.goosegame.movelogic.MoveType._
import it.scalalearn.goosegame.rosterlogic.RosterHandler
import it.scalalearn.goosegame.readout.{FinalReadout, ReadoutData, ReadoutBuilder}

import scala.annotation.tailrec

object MoveHandler {
  def movePlayer(gameState: GameState, name: String, dice: List[Int]): Either[GameError, (GameState, FinalReadout)] = {
    for {
      validDice <- validateDice(dice)
      startSquare <- gameState.getPlayerSquare(name)
    } yield {
      val move = Move(name, startSquare, startSquare, validDice)
      val startReadout = ReadoutBuilder.logStartRoll(name, startSquare, validDice)

      val moveList = makeMoveList(move, List[(MoveType, Int)]())
      val (newGameState, finalReadout) = interpretMoves(gameState, move, moveList, startReadout)

      (newGameState, finalReadout.seal())
    }
  }

  @tailrec
  def makeMoveList(firstMove: Move, squareList: List[(MoveType, Int)]): List[(MoveType, Int)] = {
    val Move(name, previousSquare, startSquare, dice) = firstMove

    previousSquare + dice.sum match {
      case LAST_SQUARE => ((LAST, LAST_SQUARE) :: squareList).reverse

      case BRIDGE_SQUARE => ((NORMAL, BRIDGE_END) :: (BRIDGE, BRIDGE_SQUARE) :: squareList).reverse

      case beyondLastSquare if beyondLastSquare > LAST_SQUARE =>
        val postBounceSquare = LAST_SQUARE - (beyondLastSquare - LAST_SQUARE)
        ((NORMAL, postBounceSquare) :: (BOUNCE, beyondLastSquare) :: squareList).reverse

      case gooseSquare if GOOSE_SQUARES(gooseSquare) =>
        makeMoveList(Move(name, gooseSquare, startSquare, dice), (GOOSE_START, gooseSquare) :: squareList)

      case normalSquare => ((NORMAL, normalSquare) :: squareList).reverse
    }
  }

  def interpretMoves(gameState: GameState, move: Move, squareList: List[(MoveType, Int)], readoutData: ReadoutData): (GameState, ReadoutData) = {
    val Move(name, _, startSquare, dice) = move

    assert(validateDice(dice).isRight)
    assert(gameState.hasPlayer(name))

    squareList.foldLeft((gameState, readoutData))(
      (stateTuple, squareTuple) => {
        val (newGameState, newReadoutData) = stateTuple
        val (moveType, square) = squareTuple

        val updatedReadout = ReadoutBuilder.appendMove(newReadoutData, name, square, moveType)
        val (prankGameState, prankReadoutData) = checkPrank(newGameState, name, square, startSquare, updatedReadout)
        val postPrankReadoutData = if (moveType == MoveType.GOOSE_START) {
          ReadoutBuilder.appendMove(prankReadoutData, name, square, MoveType.GOOSE_CONTINUE)
        } else
          prankReadoutData

        (GameState(prankGameState, name, square), postPrankReadoutData)
      }
    )
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
