package it.scalalearn.goosegame.internal.movelogic

import it.scalalearn.goosegame.internal.gamestate.GameState
import it.scalalearn.goosegame.internal.gamestate.SpecialSquares.{BRIDGE_SQUARE, BRIDGE_END, GOOSE_SQUARES, LAST_SQUARE}
import it.scalalearn.goosegame.internal.rosterlogic.RosterHandler
import it.scalalearn.goosegame.ui.errors.{DiceError, DoubledPlayerError, GameError, UnknownPlayerError}
import it.scalalearn.goosegame.ui.output.{FinalOutput, OutputData, OutputBuilder}

import scala.annotation.tailrec

object MoveHandler {
  def movePlayer(gameState: GameState, name: String, dice: List[Int]): Either[GameError, (GameState, FinalOutput)] = {
    for {
      validDice <- validateDice(dice)
      startSquare <- gameState.getPlayerSquare(name)
    } yield {
      val moveData = MoveData(name, startSquare, startSquare, validDice)
      val startOutput = OutputBuilder.logStartRoll(name, startSquare, validDice)

      val moves = computeMoves(moveData, List[Move]())
      val (finalGameState, finalOutput) = generateStateAndOutputFromMoves(gameState, moveData, moves, startOutput)

      (finalGameState, finalOutput.seal())
    }
  }

  @tailrec
  def computeMoves(moveData: MoveData, moves: List[Move]): List[Move] = {
    val MoveData(name, previousSquare, startSquare, dice) = moveData

    previousSquare + dice.sum match {
      case LAST_SQUARE => finishMoves(moves, Win(name))

      case BRIDGE_SQUARE => finishMoves(moves, Bridge(name), Stop(name, BRIDGE_END))

      case beyondLastSquare if beyondLastSquare > LAST_SQUARE =>
        val postBounceSquare = LAST_SQUARE - (beyondLastSquare - LAST_SQUARE)
        finishMoves(moves,  Bounce(name, beyondLastSquare), Stop(name, postBounceSquare))

      case gooseSquare if GOOSE_SQUARES(gooseSquare) =>
        computeMoves(MoveData(name, gooseSquare, startSquare, dice), addMove(moves, Goose(name, gooseSquare)))

      case stopSquare => finishMoves(moves, Stop(name, stopSquare))
    }
  }

  def addMove(moves: List[Move], moveToAdd: Move): List[Move] = moveToAdd :: moves

  def finishMoves(moves: List[Move], movesToAdd: Move*): List[Move] = moves.reverse ++ movesToAdd

  def generateStateAndOutputFromMoves(gameState: GameState, moveData: MoveData, moves: List[Move], outputData: OutputData): (GameState, OutputData) = {
    val MoveData(name, _, startSquare, dice) = moveData

    assert(validateDice(dice).isRight)
    assert(gameState.hasPlayer(name))

    moves.foldLeft((gameState, outputData))(
      (stateTuple, move) => {
        val (newGameState, newOutputData) = stateTuple

        val updatedOutput = OutputBuilder.appendMove(newOutputData, move)
        val updatedGameState = updateGameState(newGameState, move)
        checkPrank(updatedGameState, move.name, move.endSquare, startSquare, updatedOutput)
      }
    )
  }

  def updateGameState(gameState: GameState, move: Move): GameState = move match {
    case Win(_, _) => GameState()
    case _ => GameState(gameState, move.name, move.endSquare)
  }

  def checkPrank(gameState: GameState,
                 name: String,
                 square: Int,
                 startSquare: Int,
                 outputData: OutputData): (GameState, OutputData) = {
    val bumpNames = gameState.playersOnSquare(name, square)

    bumpNames.foldLeft((gameState, outputData))(
      (output, otherPlayer) => {
        val (newGameState, currentOutput) = output

        (GameState(newGameState, otherPlayer, startSquare),
          OutputBuilder.appendPrank(currentOutput, otherPlayer, square, startSquare))
      }
    )
  }

  def validateDice(dice: List[Int]): Either[GameError, List[Int]] =
    if (dice.forall(die => die >= 1 && die <= 6))
      Right(dice)
    else
      Left(DiceError)
}
