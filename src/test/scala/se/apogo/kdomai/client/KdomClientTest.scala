package se.apogo.kdomai.client

import org.json4s.DefaultFormats
import org.testng.Assert
import org.testng.annotations.Test

@Test(enabled = false)
class KdomClientTest {
  def client = new KdomClient("http://localhost:8080/")

  implicit val formats = DefaultFormats

  @Test
  def test_getGames(): Unit = {
    Assert.assertTrue(client.getGames().isDefined)
  }

  @Test
  def test_getNewGames(): Unit = {
    Assert.assertTrue(client.getNewGames().isDefined)
  }

  @Test
  def test_playGame(): Unit = {
    val gameAfter1Move: Option[Game] = {
      for {
        newGame: NewGame         <- client.postNewGame(2)
        gameId: String            = newGame.uuid
        playerA: PlayerWithToken <- client.postJoinGame(gameId, "PlayerA")
        playerB: PlayerWithToken <- client.postJoinGame(gameId, "PlayerB")
        game: Game               <- client.getGame(gameId)
        playerOnTurn: Player     <- game.currentPlayer
        currentPlayerWithToken   <- Seq(playerA, playerB).find(_.name == playerOnTurn.name)
        availableMoves: Moves    <- client.getAvailableMoves(gameId)
        gameAfterMove: Game      <- client.postMove(gameId, currentPlayerWithToken.uuid, moveNumber = 0)
      } yield {
        gameAfterMove
      }
    }

    Assert.assertTrue(gameAfter1Move.isDefined)
  }


}
