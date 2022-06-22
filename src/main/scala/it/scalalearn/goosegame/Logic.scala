package it.scalalearn.goosegame

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
  def movePlayer(gameState: Map[String, Int], name: String, die1: Int, die2: Int): Either[String, (Map[String, Int], String)]= {
    if (die1 < 1 || die1 > 6 || die2 < 1 || die2 > 6)
      Left("dice must be have value from 1 to 6")
    else {
      gameState.get(name) match {
        case Some(square) => {
          val newSquare = square + die1 + die2
          val status = new StringBuilder(s"$name rolls $die1, $die2. $name moves from ${if (square == 0) "Start" else square} to ")

          newSquare match {
            case LAST_SQUARE => Right(gameState + (name -> newSquare), status.append(s"$LAST_SQUARE. $name Wins!!").toString)
            case BRIDGE => Right(gameState+ (name -> BRIDGE_END), status.append(s"The Bridge. $name jumps to $BRIDGE_END").toString)
            case square if square > LAST_SQUARE => {
              val bounceTo = LAST_SQUARE - (newSquare - LAST_SQUARE)
              Right(gameState + (name -> bounceTo), status.append(s"$LAST_SQUARE. $name bounces! $name returns to $bounceTo").toString)
            }
            case square if GOOSE_SQUARES(square) => {
              val doubleTo = newSquare + die1 + die2
              Right(gameState + (name -> doubleTo), status.append(s"$newSquare, The Goose. $name moves again and goes to $doubleTo").toString)
            }
            case _ => Right(gameState + (name -> newSquare), status.append(s"$newSquare").toString)
          }
        }
        case None => Left(s"$name: unrecognized player")
      }
    }
  }
}
