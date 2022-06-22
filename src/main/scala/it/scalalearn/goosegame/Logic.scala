package it.scalalearn.goosegame

import scala.annotation.tailrec
import scala.collection.mutable

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
      val status = new mutable.StringBuilder(s"$name rolls $die1, $die2. $name moves from ${if (oldSquare == 0) "Start" else oldSquare} to ")
      
      @tailrec
      def advance(gameState: Map[String, Int], square: Int, status: mutable.StringBuilder): (Map[String, Int], mutable.StringBuilder) = {
        square + die1 + die2 match {
          case LAST_SQUARE => (Map[String, Int](), status.append(s"$LAST_SQUARE. $name Wins!!"))

          case BRIDGE =>
            val (prankGameState, prankStatus) = checkPrank(gameState, BRIDGE_END)
            (prankGameState + (name -> BRIDGE_END), status.append(s"The Bridge. $name jumps to $BRIDGE_END").append(prankStatus))

          case newSquare if newSquare > LAST_SQUARE =>
            val bounceTo = LAST_SQUARE - (newSquare - LAST_SQUARE)
            val (prankGameState, prankStatus) = checkPrank(gameState, bounceTo)
            (prankGameState + (name -> bounceTo), status.append(s"$LAST_SQUARE. $name bounces! $name returns to $bounceTo").append(prankStatus))

          case newSquare if GOOSE_SQUARES(newSquare) =>
            val (prankGameState, prankStatus) = checkPrank(gameState, newSquare)
            advance(prankGameState + (name -> (newSquare + die1 + die2)),
                    newSquare,
                    status.append(s"$newSquare, The Goose").append(prankStatus).append(s". $name moves again and goes to "))
          
          case newSquare =>
            val (prankGameState, prankStatus) = checkPrank(gameState, newSquare)
            (prankGameState + (name -> newSquare), status.append(s"$newSquare").append(prankStatus))
        }
      }

      def checkPrank(gameState: Map[String, Int], newSquare: Int): (Map[String, Int], mutable.StringBuilder) = {
        val bumpNames = gameState.filter({
          case (otherName: String, otherSquare: Int) => otherName != name && otherSquare == newSquare
        }).keys
        bumpNames.foldLeft((gameState, new mutable.StringBuilder()))((state, player) =>
          (state._1 + (player -> oldSquare), state._2.append(s". On $newSquare there is $player, who returns to $oldSquare")))
      }

      val (newGameState, newStatus) = advance(oldGameState, oldSquare, status)

      (newGameState, newStatus.toString)
    }
  }

  def validateDie(die: Int): Either[String, Int] =
    if (die >= 1 && die <= 6) Right(die) else Left("Dice must have value from 1 to 6")
}
