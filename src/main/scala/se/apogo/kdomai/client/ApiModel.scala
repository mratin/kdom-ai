package se.apogo.kdomai.client

case class Tile(terrain: String, crowns: Int) extends JsonSerializable

case class Domino(number: Int, tile1: Tile, tile2: Tile) extends JsonSerializable

case class DraftDomino(player: Option[Player], domino: Domino) extends JsonSerializable

case class Draft(dominoes: Seq[DraftDomino]) extends JsonSerializable

case class Player(name: String) extends JsonSerializable
case class PlayerWithToken(name: String, uuid: String, callBackUrl: Option[String]) extends JsonSerializable

case class Position(row: Int, col: Int)

case class PlacedTile(position: Position, tile: Tile)

case class Score(total: Int, areaScores: Seq[Int], centerBonus: Int, completeBonus: Int)

case class Kingdom(player: Player, placedTiles: Seq[PlacedTile], score: Score) extends JsonSerializable

case class Game(uuid: String,
                created: String,
                updated: String,
                kingdoms: Seq[Kingdom],
                currentDraft: Draft, previousDraft: Draft,
                currentPlayer: Option[Player],
                gameOver: Boolean,
                turn: Int)
  extends JsonSerializable

case class PlacedDomino(domino: Domino, tile1Position: Position, tile2Position: Position) extends JsonSerializable

case class Move(number: Int, chosenDomino: Option[Domino], placedDomino: Option[PlacedDomino]) extends JsonSerializable

case class NewGame(uuid: String, created: String, updated: String, numberOfPlayers: Int, joinedPlayers: Seq[Player]) extends JsonSerializable

case class NewGames(newGames: Seq[NewGame]) extends JsonSerializable

case class BriefGame(uuid: String, created: String, updated: String, players: Seq[Player],
                     playerOnTurn: Option[Player], round: Int, turn: Int, gameOver: Boolean) extends JsonSerializable

case class BriefGames(games: Seq[BriefGame]) extends JsonSerializable
case class Moves(moves: Seq[Move]) extends JsonSerializable
