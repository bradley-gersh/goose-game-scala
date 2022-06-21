package it.scalalearn.goosegame

class GameState(val players: List[Player]) {
    def addPlayer(newPlayer: Player) = {
        val newPlayers = players :+ newPlayer
        println("players: " + newPlayers.map(_.name))
        new GameState(newPlayers)
    }
}

object GameState {
    def apply() = new GameState(List[Player]())
    def apply(players: List[Player]) = new GameState(players)
}