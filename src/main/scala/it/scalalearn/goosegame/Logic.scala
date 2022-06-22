package it.scalalearn.goosegame

import scala.annotation.tailrec
object Logic {
  final val LAST_SQUARE = 63
  final val BRIDGE = 6
  final val BRIDGE_END = 12
  final val GOOSE_SQUARES = Set(5, 9, 14, 18, 23, 27)

  def addPlayer(gameState: Map[String, Int], newPlayer: String): Either[String, (Map[String, Int], String)] = {
    if (gameState.contains(newPlayer)) {
      Left(s"$newPlayer: already existing player")
    } else {
      val newGameState = gameState + (newPlayer -> 0)
      Right(newGameState, s"players: ${newGameState.keys.mkString(", ")}")
    }
  }

  // check order of players? last player, next player?
  def movePlayer(oldGameState: Map[String, Int], name: String, die1: Int, die2: Int): Either[String, (Map[String, Int], String)] = {
    for {
      die1 <- validateDie(die1)
      die2 <- validateDie(die2)
      oldSquare <- oldGameState.get(name).toRight(s"$name: unrecognized player")
    } yield {
      val status = new StringBuilder(s"$name rolls $die1, $die2. $name moves from ${if (oldSquare == 0) "Start" else oldSquare} to ")
      
      @tailrec
      def advance(gameState: Map[String, Int], square: Int, status: StringBuilder): (Map[String, Int], StringBuilder) = (square + die1 + die2) match {
        case LAST_SQUARE => (gameState + (name -> LAST_SQUARE), status.append(s"$LAST_SQUARE. $name Wins!!"))
        case BRIDGE => (gameState + (name -> BRIDGE_END), status.append(s"The Bridge. $name jumps to $BRIDGE_END"))
        case newSquare if newSquare > LAST_SQUARE => {
          val bounceTo = LAST_SQUARE - (newSquare - LAST_SQUARE)
          (gameState + (name -> bounceTo), status.append(s"$LAST_SQUARE. $name bounces! $name returns to $bounceTo"))
        }
        case newSquare if GOOSE_SQUARES(newSquare) => {
          advance(gameState + (name -> (newSquare + die1 + die2)), newSquare, status.append(s"$newSquare, The Goose. $name moves again and goes to "))
        }
        case newSquare => (gameState + (name -> newSquare), status.append(s"$newSquare"))
      }

      val (newGameState, newStatus) = advance(oldGameState, oldSquare, status)

      (newGameState, newStatus.toString)
    }
  }

  def validateDie(die: Int): Either[String, Int] =
    if (die >= 1 && die <= 6) Right(die) else Left("dice must be have value from 1 to 6")
}
