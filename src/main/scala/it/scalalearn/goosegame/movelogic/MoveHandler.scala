package it.scalalearn.goosegame.movelogic

import it.scalalearn.goosegame.errors.{DiceError, DoubledPlayerError, GameError, UnknownPlayerError}
import it.scalalearn.goosegame.gamestate.GameState
import it.scalalearn.goosegame.gamestate.SpecialSquares.{BRIDGE, BRIDGE_END, GOOSE_SQUARES, LAST_SQUARE}
import it.scalalearn.goosegame.movelogic.MoveType
import it.scalalearn.goosegame.rosterlogic.RosterHandler
import it.scalalearn.goosegame.readout.{FinalReadout, ReadoutData, ReadoutBuilder}

import scala.annotation.tailrec
import scala.collection.mutable

object MoveHandler {
  def movePlayer(gameState: GameState, name: String, dice: List[Int]): Either[GameError, (GameState, FinalReadout)] = {
    for {
      validDice <- validateDice(dice)
      startSquare <- gameState.getPlayerSquare(name)
    } yield {
      val move = Move(name, startSquare, startSquare, validDice)
      val startReadout = ReadoutBuilder.logStartRoll(name, startSquare, validDice)

      val squareList = makeSquareList(move, List[Int]())
      val (newGameState, finalReadout) = interpretMoves(gameState, move, squareList, startReadout)

      (newGameState, finalReadout.seal())
    }
  }

  @tailrec
  def makeSquareList(firstMove: Move, squareList: List[Int]): List[Int] = {
    val Move(name, previousSquare, startSquare, dice) = firstMove

    previousSquare + dice.sum match {
      case BRIDGE =>
        (BRIDGE_END :: BRIDGE :: squareList).reverse

      case beyondLastSquare if beyondLastSquare > LAST_SQUARE =>
        val postBounceSquare = LAST_SQUARE - (beyondLastSquare - LAST_SQUARE)
        (postBounceSquare :: beyondLastSquare :: squareList).reverse

      case gooseSquare if GOOSE_SQUARES(gooseSquare) =>
        val nextMove = Move(name, gooseSquare, startSquare, dice)
        makeSquareList(nextMove, gooseSquare :: squareList)

      case normalSquare =>
        (normalSquare :: squareList).reverse
    }
  }

  def interpretMoves(gameState: GameState, move: Move, squareList: List[Int], readoutData: ReadoutData): (GameState, ReadoutData) = {
    val Move(name, _, startSquare, dice) = move

    assert(validateDice(dice).isRight)
    assert(gameState.hasPlayer(name))

    squareList.foldLeft((gameState, readoutData))(
      (stateTuple, square) => {
        val (newGameState, newReadoutData) = stateTuple

        val moveType = getMoveType(square)

        val updatedReadout = ReadoutBuilder.appendMove(newReadoutData, name, square, moveType)
        val (prankGameState, prankReadoutData) = checkPrank(newGameState, name, square, startSquare, updatedReadout)
        val gooseReadoutData = if (moveType == MoveType.GOOSE_START) {
          ReadoutBuilder.appendMove(prankReadoutData, name, square, MoveType.GOOSE_CONTINUE)
        } else
          prankReadoutData

        (GameState(prankGameState, name, square), gooseReadoutData)
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

  def getMoveType(square: Int): MoveType = square match {
    case LAST_SQUARE => MoveType.LAST
    case BRIDGE => MoveType.BRIDGE
    case beyondLastSquare if beyondLastSquare > LAST_SQUARE => MoveType.BOUNCE
    case gooseSquare if GOOSE_SQUARES(gooseSquare) => MoveType.GOOSE_START
    case _ => MoveType.NORMAL
  }
}
