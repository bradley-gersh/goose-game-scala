package it.scalalearn.goosegame.ui.output

import it.scalalearn.goosegame.internal.gamestate.GameState
import it.scalalearn.goosegame.internal.movelogic.Move
import it.scalalearn.goosegame.internal.movelogic.{Bounce, Bridge, GooseEnd, GooseStart, Normal, Win}
import it.scalalearn.goosegame.ui.output.OutputMessages.{LIST_PLAYERS_MSG, MID_ROLL_BOUNCE_MSG, MID_ROLL_BRIDGE_MSG,
  MID_ROLL_GOOSE_CONTINUE_MSG, MID_ROLL_GOOSE_START_MSG, MID_ROLL_PRANK_MSG, START_ROLL_MSG, WIN_MSG}

object OutputBuilder {
  def appendMessage(outputData: OutputData, newMessage: String): OutputData =
    OutputData(newMessage :: outputData.messages)

  def startLog(message: String): OutputData =
    OutputData(List(message))

  def appendMove(outputData: OutputData, move: Move): OutputData = {
    move match {
      case Bounce(name, _) => appendMessage(outputData, MID_ROLL_BOUNCE_MSG(name))
      case Bridge(name, _) => appendMessage(outputData, MID_ROLL_BRIDGE_MSG(name))
      case GooseStart(_, endSquare) => appendMessage(outputData, MID_ROLL_GOOSE_START_MSG(endSquare))
      case GooseEnd(name, _) => appendMessage(outputData, MID_ROLL_GOOSE_CONTINUE_MSG(name))
      case Normal(_, endSquare) => appendMessage(outputData, endSquare.toString)
      case Win(name, _) => appendMessage(outputData, WIN_MSG(name))
    }
  }

  def appendPrank(outputData: OutputData,
                  otherPlayer: String,
                  square: Int,
                  startSquare: Int): OutputData =
    appendMessage(outputData, MID_ROLL_PRANK_MSG(otherPlayer, square, startSquare))

  def logAddPlayer(gameState: GameState): OutputData =
    startLog(LIST_PLAYERS_MSG(gameState))

  def logStartRoll(name: String, previousSquare: Int, dice: List[Int]): OutputData =
    startLog(START_ROLL_MSG(name, previousSquare, dice))
}
