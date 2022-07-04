package it.scalalearn.goosegame.internal.movelogic

import it.scalalearn.goosegame.internal.gamestate.GameState
import it.scalalearn.goosegame.internal.gamestate.SpecialSquares.{BRIDGE_SQUARE, BRIDGE_END, GOOSE_SQUARES, LAST_SQUARE}
import it.scalalearn.goosegame.internal.rosterlogic.RosterHandler
import it.scalalearn.goosegame.ui.errors.{DiceError, DoubledPlayerError, GameError, UnknownPlayerError}
import it.scalalearn.goosegame.ui.readout.{FinalReadout, ReadoutData, ReadoutBuilder}

import scala.annotation.tailrec

object MoveHandler {
  def movePlayer(gameState: GameState, name: String, dice: List[Int]): Either[GameError, (GameState, FinalReadout)] = {
    for {
      validDice <- validateDice(dice)
      startSquare <- gameState.getPlayerSquare(name)
    } yield {
      val move = RawMove(name, startSquare, startSquare, validDice)
      val startReadout = ReadoutBuilder.logStartRoll(name, startSquare, validDice)

      val moveList = makeMoveList(move, List[ProjectedMove]())
      val (newGameState, finalReadout) = interpretMoves(gameState, move, moveList, startReadout)

      (newGameState, finalReadout.seal())
    }
  }

  @tailrec
  def makeMoveList(firstMove: RawMove, squareList: List[ProjectedMove]): List[ProjectedMove] = {
    val RawMove(name, previousSquare, startSquare, dice) = firstMove

    previousSquare + dice.sum match {
      case LAST_SQUARE => (ProjectedMove(MoveType.LAST, LAST_SQUARE) :: squareList).reverse

      case BRIDGE_SQUARE => (ProjectedMove(MoveType.NORMAL, BRIDGE_END) ::
        ProjectedMove(MoveType.BRIDGE, BRIDGE_SQUARE) ::
        squareList).reverse

      case beyondLastSquare if beyondLastSquare > LAST_SQUARE =>
        val postBounceSquare = LAST_SQUARE - (beyondLastSquare - LAST_SQUARE)
        (ProjectedMove(MoveType.NORMAL, postBounceSquare) ::
          ProjectedMove(MoveType.BOUNCE, beyondLastSquare) ::
          squareList).reverse

      case gooseSquare if GOOSE_SQUARES(gooseSquare) =>
        makeMoveList(RawMove(name, gooseSquare, startSquare, dice), ProjectedMove(MoveType.GOOSE_START, gooseSquare) :: squareList)

      case normalSquare => (ProjectedMove(MoveType.NORMAL, normalSquare) :: squareList).reverse
    }
  }

  def interpretMoves(gameState: GameState, move: RawMove, squareList: List[ProjectedMove], readoutData: ReadoutData): (GameState, ReadoutData) = {
    val RawMove(name, _, startSquare, dice) = move

    assert(validateDice(dice).isRight)
    assert(gameState.hasPlayer(name))

    squareList.foldLeft((gameState, readoutData))(
      (stateTuple, squareTuple) => {
        val (newGameState, newReadoutData) = stateTuple
        val ProjectedMove(moveType, square) = squareTuple

        val updatedReadout = ReadoutBuilder.appendMove(newReadoutData, name, square, moveType)
        val (prankGameState, prankReadoutData) = checkPrank(newGameState, name, square, startSquare, updatedReadout)

        val postPrankGameState = if (moveType == MoveType.LAST)
          GameState()
        else
          GameState(prankGameState, name, square)

        val postPrankReadoutData = if (moveType == MoveType.GOOSE_START)
          ReadoutBuilder.appendMove(prankReadoutData, name, square, MoveType.GOOSE_CONTINUE)
        else
          prankReadoutData

        (postPrankGameState, postPrankReadoutData)
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
