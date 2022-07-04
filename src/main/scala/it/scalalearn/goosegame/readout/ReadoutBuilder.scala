package it.scalalearn.goosegame.readout

import it.scalalearn.goosegame.gamestate.GameState
import it.scalalearn.goosegame.readout.IntermediateReadout
import it.scalalearn.goosegame.readout.ReadoutMessages.{LIST_PLAYERS_MSG, MID_ROLL_BOUNCE_MSG, MID_ROLL_BRIDGE_MSG, MID_ROLL_GOOSE_CONTINUE_MSG, MID_ROLL_GOOSE_START_MSG, MID_ROLL_PRANK_MSG, START_ROLL_MSG, WIN_MSG}

object ReadoutBuilder {
  def appendMessage(intermediateReadout: IntermediateReadout, newMessage: String): IntermediateReadout =
    IntermediateReadout(newMessage :: intermediateReadout.messages)

  def startLog(message: String): IntermediateReadout =
    IntermediateReadout(List(message))

  def appendBounce(intermediateReadout: IntermediateReadout, name: String, bounceToSquare: Int): IntermediateReadout =
    appendMessage(intermediateReadout, MID_ROLL_BOUNCE_MSG(name, bounceToSquare))

  def appendBridge(intermediateReadout: IntermediateReadout, name: String): IntermediateReadout =
    appendMessage(intermediateReadout, MID_ROLL_BRIDGE_MSG(name))

  def appendGooseStart(intermediateReadout: IntermediateReadout, newSquare: Int): IntermediateReadout =
    appendMessage(intermediateReadout, MID_ROLL_GOOSE_START_MSG(newSquare))

  def appendGooseContinue(intermediateReadout: IntermediateReadout, name: String): IntermediateReadout =
    appendMessage(intermediateReadout, MID_ROLL_GOOSE_CONTINUE_MSG(name))

  def appendWin(intermediateReadout: IntermediateReadout, name: String): IntermediateReadout =
    appendMessage(intermediateReadout, WIN_MSG(name))

  def appendNormal(intermediateReadout: IntermediateReadout, square: Int): IntermediateReadout =
    appendMessage(intermediateReadout, square.toString)

  def appendPrank(intermediateReadout: IntermediateReadout,
                  otherPlayer: String,
                  square: Int,
                  startSquare: Int): IntermediateReadout =
    appendMessage(intermediateReadout, MID_ROLL_PRANK_MSG(otherPlayer, square, startSquare))

  def logAddPlayer(gameState: GameState): IntermediateReadout =
    startLog(LIST_PLAYERS_MSG(gameState))

  def logStartRoll(name: String, previousSquare: Int, dice: List[Int]): IntermediateReadout =
    startLog(START_ROLL_MSG(name, previousSquare, dice))
}
