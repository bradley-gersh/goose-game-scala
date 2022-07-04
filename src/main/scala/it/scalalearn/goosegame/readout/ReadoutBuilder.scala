package it.scalalearn.goosegame.readout

import it.scalalearn.goosegame.gamestate.GameState
import it.scalalearn.goosegame.readout.ReadoutData
import it.scalalearn.goosegame.readout.ReadoutMessages.{LIST_PLAYERS_MSG, MID_ROLL_BOUNCE_MSG, MID_ROLL_BRIDGE_MSG,
  MID_ROLL_GOOSE_CONTINUE_MSG, MID_ROLL_GOOSE_START_MSG, MID_ROLL_PRANK_MSG, START_ROLL_MSG, WIN_MSG}

object ReadoutBuilder {
  def appendMessage(readoutData: ReadoutData, newMessage: String): ReadoutData =
    ReadoutData(newMessage :: readoutData.messages)

  def startLog(message: String): ReadoutData =
    ReadoutData(List(message))

  def appendBounce(readoutData: ReadoutData, name: String, bounceToSquare: Int): ReadoutData =
    appendMessage(readoutData, MID_ROLL_BOUNCE_MSG(name, bounceToSquare))

  def appendBridge(readoutData: ReadoutData, name: String): ReadoutData =
    appendMessage(readoutData, MID_ROLL_BRIDGE_MSG(name))

  def appendGooseStart(readoutData: ReadoutData, newSquare: Int): ReadoutData =
    appendMessage(readoutData, MID_ROLL_GOOSE_START_MSG(newSquare))

  def appendGooseContinue(readoutData: ReadoutData, name: String): ReadoutData =
    appendMessage(readoutData, MID_ROLL_GOOSE_CONTINUE_MSG(name))

  def appendWin(readoutData: ReadoutData, name: String): ReadoutData =
    appendMessage(readoutData, WIN_MSG(name))

  def appendNormal(readoutData: ReadoutData, square: Int): ReadoutData =
    appendMessage(readoutData, square.toString)

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
