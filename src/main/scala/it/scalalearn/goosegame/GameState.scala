package it.scalalearn.goosegame

class GameState(val players: List[Player], val status: String = "") {
    def getPlayers: List[Player] = players

    def addPlayer(newPlayer: Player): GameState = {
        if (players.exists(_.name == newPlayer.name)) {
            setStatus(s"${newPlayer.name}: already existing player")
        } else {
            val newPlayers = players :+ newPlayer
            new GameState(newPlayers, s"players: ${newPlayers.map(_.name).mkString(", ")}")
        }
    }
    
    def addPlayer(newPlayerName: String): GameState =
        addPlayer(Player(newPlayerName, 0))

    def setStatus(newStatus: String): GameState =
        new GameState(players, newStatus)
        
}

object GameState {
    def apply() = new GameState(List[Player]())
    def apply(players: List[Player]) = new GameState(players)
    def apply(players: List[Player], status: String) = new GameState(players, status) 
}