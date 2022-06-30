package it.scalalearn.goosegame

import scala.annotation.tailrec
import scala.collection.mutable

object Logic {
  final val LAST_SQUARE = 63
  final val BRIDGE = 6
  final val BRIDGE_END = 12
  final val GOOSE_SQUARES = Set(5, 9, 14, 18, 23, 27)

  def addPlayer(squaresByPlayerNames: Map[String, Int], newPlayer: String): Either[String, (Map[String, Int], String)] = {
    if (squaresByPlayerNames.contains(newPlayer)) {
      Left(s"$newPlayer: already existing player")
    } else {
      val updatedSquaresByPlayerNames = squaresByPlayerNames + (newPlayer -> 0)
      Right(updatedSquaresByPlayerNames, s"players: ${updatedSquaresByPlayerNames.keys.mkString(", ")}")
    }
  }

  def movePlayer(squaresByPlayerNames: Map[String, Int], name: String, dice: List[Int]): Either[String, (Map[String, Int], String)] = {
    for {
      validatedDice <- validateDice(dice)
      oldSquare <- squaresByPlayerNames.get(name).toRight(s"$name: unrecognized player")
    } yield {
      val status = new mutable.StringBuilder(s"$name rolls ${validatedDice.mkString(", ")}. $name moves from ${if (oldSquare == 0) "Start" else oldSquare} to ")
      
      @tailrec
      def advance(squaresByPlayerNames: Map[String, Int], square: Int, status: mutable.StringBuilder): (Map[String, Int], mutable.StringBuilder) = {
        square + dice.sum match {
          case LAST_SQUARE => (Map[String, Int](), status.append(s"$LAST_SQUARE. $name Wins!!"))

          case BRIDGE =>
            val (prankGameState, prankStatus) = checkPrank(squaresByPlayerNames, BRIDGE_END)
            (prankGameState + (name -> BRIDGE_END), status.append(s"The Bridge. $name jumps to $BRIDGE_END").append(prankStatus))

          case newSquare if newSquare > LAST_SQUARE =>
            val bounceTo = LAST_SQUARE - (newSquare - LAST_SQUARE)
            val (prankGameState, prankStatus) = checkPrank(squaresByPlayerNames, bounceTo)
            (prankGameState + (name -> bounceTo), status.append(s"$LAST_SQUARE. $name bounces! $name returns to $bounceTo").append(prankStatus))

          case newSquare if GOOSE_SQUARES(newSquare) =>
            val (prankGameState, prankStatus) = checkPrank(squaresByPlayerNames, newSquare)
            advance(prankGameState + (name -> (newSquare + dice.sum)),
                    newSquare,
                    status.append(s"$newSquare, The Goose").append(prankStatus).append(s". $name moves again and goes to "))
          
          case newSquare =>
            val (prankGameState, prankStatus) = checkPrank(squaresByPlayerNames, newSquare)
            (prankGameState + (name -> newSquare), status.append(s"$newSquare").append(prankStatus))
        }
      }

      def checkPrank(squaresByPlayerNames: Map[String, Int], newSquare: Int): (Map[String, Int], mutable.StringBuilder) = {
        val bumpNames = squaresByPlayerNames.filter({
          case (otherName: String, otherSquare: Int) => otherName != name && otherSquare == newSquare
        }).keys
        bumpNames.foldLeft((squaresByPlayerNames, new mutable.StringBuilder()))((state, player) =>
          (state._1 + (player -> oldSquare), state._2.append(s". On $newSquare there is $player, who returns to $oldSquare")))
      }

      val (updatedSquaresByPlayerNames, newStatus) = advance(squaresByPlayerNames, oldSquare, status)

      (updatedSquaresByPlayerNames, newStatus.toString)
    }
  }

  def validateDice(dice: List[Int]): Either[String, List[Int]] =
    if (dice.forall(die => die >= 1 && die <= 6)) Right(dice) else Left("Dice must have value from 1 to 6")
}
