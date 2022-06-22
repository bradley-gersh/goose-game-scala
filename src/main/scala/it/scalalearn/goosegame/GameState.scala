package it.scalalearn.goosegame

class GameState(val players: Map[String, Int], val status: String = "") {
  final val LAST_SQUARE = 63
  final val BRIDGE = 6
  final val BRIDGE_END = 12

  def getPlayers: Map[String, Int] = players

  def addPlayer(newPlayer: String): GameState = {
    if (players.contains(newPlayer)) {
      setStatus(s"$newPlayer: already existing player")
    } else {
      val newPlayers = players + (newPlayer -> 0)
      GameState(newPlayers, s"players: ${newPlayers.keys.mkString(", ")}")
    }
  }
  // check order of players? last player, next player?
  def movePlayer(name: String, die1: Int, die2: Int): GameState = {
    players.get(name) match {
      case Some(square) => {
        val newSquare = square + die1 + die2
        val newStatus = new StringBuilder(s"$name rolls $die1, $die2. $name moves from ${if (square == 0) "Start" else square} to ")

        newSquare match {
          case LAST_SQUARE => GameState(players + (name -> newSquare), newStatus.append(s"$LAST_SQUARE. $name Wins!!").toString)
          case BRIDGE => GameState(players + (name -> BRIDGE_END), newStatus.append(s"The Bridge. $name jumps to $BRIDGE_END").toString)
          case square if square > LAST_SQUARE => {
            val bounceTo = LAST_SQUARE - (newSquare - LAST_SQUARE)
            GameState(players + (name -> bounceTo), newStatus.append(s"$LAST_SQUARE. $name bounces! $name returns to $bounceTo").toString)
          }
          case _ => GameState(players + (name -> newSquare), newStatus.append(s"$newSquare").toString)
        }
      }
      case None => GameState(players, s"$name: unrecognized player")
    }
  }
  
  def setStatus(newStatus: String): GameState =
    GameState(players, newStatus)
    
}

object GameState {
  def apply() = new GameState(Map[String, Int]())
  def apply(players: String*) = new GameState(players.foldLeft(Map[String, Int]())((acc, player) => acc + (player -> 0)))
  def apply(players: Map[String, Int]) = new GameState(players)
  def apply(players: Map[String, Int], status: String) = new GameState(players, status) 
}