package it.scalalearn.goosegame.ui.readout

import it.scalalearn.goosegame.internal.gamestate.GameState
import it.scalalearn.goosegame.internal.movelogic.MoveType
import it.scalalearn.goosegame.internal.movelogic.MoveType.{BOUNCE, BRIDGE, GOOSE_CONTINUE, GOOSE_START, LAST, NORMAL}
import it.scalalearn.goosegame.ui.readout.ReadoutMessages.{LIST_PLAYERS_MSG, MID_ROLL_BOUNCE_MSG, MID_ROLL_BRIDGE_MSG,
  MID_ROLL_GOOSE_CONTINUE_MSG, MID_ROLL_GOOSE_START_MSG, MID_ROLL_PRANK_MSG, START_ROLL_MSG, WIN_MSG}

object ReadoutBuilder {
  def appendMessage(readoutData: ReadoutData, newMessage: String): ReadoutData =
    ReadoutData(newMessage :: readoutData.messages)

  def startLog(message: String): ReadoutData =
    ReadoutData(List(message))

  def appendMove(readoutData: ReadoutData, name: String, square: Int, moveType: MoveType): ReadoutData = {
    moveType match {
      case BOUNCE => appendMessage(readoutData, MID_ROLL_BOUNCE_MSG(name))
      case BRIDGE => appendMessage(readoutData, MID_ROLL_BRIDGE_MSG(name))
      case GOOSE_START => appendMessage(readoutData, MID_ROLL_GOOSE_START_MSG(square))
      case GOOSE_CONTINUE => appendMessage(readoutData, MID_ROLL_GOOSE_CONTINUE_MSG(name))
      case NORMAL => appendMessage(readoutData, square.toString)
      case LAST => appendMessage(readoutData, WIN_MSG(name))
    }
  }

  def appendPrank(readoutData: ReadoutData,
                  otherPlayer: String,
                  square: Int,
                  startSquare: Int): ReadoutData =
    appendMessage(readoutData, MID_ROLL_PRANK_MSG(otherPlayer, square, startSquare))

  def logAddPlayer(gameState: GameState): ReadoutData =
    startLog(LIST_PLAYERS_MSG(gameState))

  def logStartRoll(name: String, previousSquare: Int, dice: List[Int]): ReadoutData =
    startLog(START_ROLL_MSG(name, previousSquare, dice))
}
