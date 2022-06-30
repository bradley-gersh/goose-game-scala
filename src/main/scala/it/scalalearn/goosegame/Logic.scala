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
      val (updatedSquaresByPlayerNames, newStatus) = advance(squaresByPlayerNames, name, oldSquare, oldSquare, dice, status)

      (updatedSquaresByPlayerNames, newStatus.toString)
    }
  }

  @tailrec
  def advance(squaresByPlayerNames: Map[String, Int], name: String, oldSquare: Int, startSquare: Int, dice: List[Int], status: mutable.StringBuilder): (Map[String, Int], mutable.StringBuilder) = {
    assert(validateDice(dice).isRight)
    assert(squaresByPlayerNames.contains(name))

    oldSquare + dice.sum match {
      case LAST_SQUARE => (Map[String, Int](), status.append(s"$LAST_SQUARE. $name Wins!!"))

      case BRIDGE =>
        val (prankGameState, prankStatus) = checkPrank(squaresByPlayerNames, name, BRIDGE_END, startSquare)
        (prankGameState + (name -> BRIDGE_END), status.append(s"The Bridge. $name jumps to $BRIDGE_END").append(prankStatus))

      case newSquare if newSquare > LAST_SQUARE =>
        val bounceTo = LAST_SQUARE - (newSquare - LAST_SQUARE)
        val (prankGameState, prankStatus) = checkPrank(squaresByPlayerNames, name, bounceTo, startSquare)
        (prankGameState + (name -> bounceTo), status.append(s"$LAST_SQUARE. $name bounces! $name returns to $bounceTo").append(prankStatus))

      case newSquare if GOOSE_SQUARES(newSquare) =>
        val (prankGameState, prankStatus) = checkPrank(squaresByPlayerNames, name, newSquare, startSquare)
        advance(prankGameState + (name -> (newSquare + dice.sum)),
          name,
          newSquare,
          startSquare,
          dice,
          status.append(s"$newSquare, The Goose").append(prankStatus).append(s". $name moves again and goes to "))

      case newSquare =>
        val (prankGameState, prankStatus) = checkPrank(squaresByPlayerNames, name, newSquare, startSquare)
        (prankGameState + (name -> newSquare), status.append(s"$newSquare").append(prankStatus))
    }
  }

  def checkPrank(squaresByPlayerNames: Map[String, Int], name: String, newSquare: Int, startSquare: Int): (Map[String, Int], mutable.StringBuilder) = {
    assert(squaresByPlayerNames.contains(name))

    val bumpNames = squaresByPlayerNames.filter({
      case (otherName: String, otherSquare: Int) => otherName != name && otherSquare == newSquare
    }).keys

    bumpNames.foldLeft((squaresByPlayerNames, new mutable.StringBuilder()))((state, player) =>
      (state._1 + (player -> startSquare), state._2.append(s". On $newSquare there is $player, who returns to $startSquare")))
  }

  def validateDice(dice: List[Int]): Either[String, List[Int]] =
    if (dice.forall(die => die >= 1 && die <= 6)) Right(dice) else Left("Dice must have value from 1 to 6")
}
