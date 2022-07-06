package it.scalalearn.goosegame.internal.events

import it.scalalearn.goosegame.internal.gamestate.GameState
import it.scalalearn.goosegame.internal.gamestate.SpecialSquares.{BRIDGE_END, BRIDGE_SQUARE, GOOSE_SQUARES, LAST_SQUARE}
import it.scalalearn.goosegame.ui.errors.{DiceError, GameError}

import scala.annotation.tailrec

object MoveScriptWriter {
  private case class MoveData(name: String, previousSquare: Int, startSquare: Int, dice: List[Int])

  def movePlayer(gameState: GameState, name: String, dice: List[Int]): Either[GameError, List[Event]] = {
    for {
      validDice <- validateDice(dice)
      startSquare <- gameState.getPlayerSquare(name)
    } yield {
      val roll = Roll(name, startSquare, validDice)
      val moves = movePlayerHelper(MoveData(name, startSquare, startSquare, validDice), List[Move]())
      val prankMove = prank(gameState, name, moves.head.endSquare, startSquare)

      finalizeEvents(roll, moves, prankMove)
    }
  }

  @tailrec
  private def movePlayerHelper(moveData: MoveData, moves: List[Move]): List[Move] = {
    val MoveData(name, previousSquare, startSquare, dice) = moveData

    previousSquare + dice.sum match {
      case LAST_SQUARE => Win(name) :: moves

      case BRIDGE_SQUARE => Stop(name, BRIDGE_END) :: Bridge(name) :: moves

      case beyondLastSquare if beyondLastSquare > LAST_SQUARE =>
        val postBounceSquare = LAST_SQUARE - (beyondLastSquare - LAST_SQUARE)
        Stop(name, postBounceSquare) :: Bounce(name, beyondLastSquare) :: moves

      case gooseSquare if GOOSE_SQUARES(gooseSquare) =>
        movePlayerHelper(MoveData(name, gooseSquare, startSquare, dice), Goose(name, gooseSquare) :: moves)

      case stopSquare => Stop(name, stopSquare) :: moves
    }
  }

  private def prank(gameState: GameState, name: String, endSquare: Int, startSquare: Int): List[Move] = {
    val prankedNames = gameState.playersOnSquare(name, endSquare)
    prankedNames.map(otherName => Prank(otherName, endSquare, startSquare))
  }

  private def finalizeEvents(roll: Roll, moves: List[Move], prankMove: List[Move]): List[Event] =
    roll +: (prankMove ++ moves).reverse

  private def validateDice(dice: List[Int]): Either[GameError, List[Int]] =
    if (dice.forall(die => die >= 1 && die <= 6))
      Right(dice)
    else
      Left(DiceError)
}
