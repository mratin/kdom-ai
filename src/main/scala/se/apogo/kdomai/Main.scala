package se.apogo.kdomai

import org.slf4j.{Logger, LoggerFactory}
import se.apogo.kdomai.client._

object Main extends App {
  val logger: Logger = LoggerFactory.getLogger(getClass)

  val kdomApiUrl = "http://localhost:8080/" // Replace with actual kdom host

  val client = new KdomClient(kdomApiUrl)

  val playerName: String = "Kingdumino"

  val pollTimeMs: Int = 1000

  playGames(Nil)

  def sleep() = Thread.sleep(pollTimeMs)

  def playGames(myGames: Seq[MyGame]): Unit = {
    if (myGames.isEmpty) {
      // We're not playing any game.
      // Look for a game to join:

      val joinedGames: Seq[MyGame] = {
        for {
          newGame         <- client.getNewGames().get.newGames
          gameId           = newGame.uuid
          playerWithToken <- client.postJoinGame(gameId, playerName)
        } yield {
          MyGame(gameId, playerWithToken)
        }
      }

      sleep()
      playGames(joinedGames)
    }
    else {
      // Report any finished games:
      val finishedGames: Seq[Game] = {
        for {
          myGame <- myGames
          game   <- client.getGame(myGame.gameId)
          if game.gameOver
        } yield {
          game
        }
      }

      for (finishedGame <- finishedGames) {
        logger.info(s"Game finished: ${finishedGame.uuid}")
      }

      val myUnfinishedGames: Seq[MyGame] = myGames.filterNot(finishedGames.contains)

      // Post my turns:
      for {
        myGame         <- myUnfinishedGames
        game           <- client.getGame(myGame.gameId)
        currentPlayer  <- game.currentPlayer
        if currentPlayer.name == myGame.playerWithToken.name
      } {
        // TODO: INSERT INTELLIGENCE HERE
        // availableMoves = client.getAvailableMoves(myGame.gameId)
        val chosenMoveNumber: Int = 0
        client.postMove(myGame.gameId, myGame.playerWithToken.uuid, chosenMoveNumber)
      }

      sleep()
      playGames(myUnfinishedGames)
    }
  }
}

case class MyGame(gameId: String, playerWithToken: PlayerWithToken)
