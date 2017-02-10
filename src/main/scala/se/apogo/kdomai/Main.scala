package se.apogo.kdomai

import org.slf4j.{Logger, LoggerFactory}
import se.apogo.kdomai.client._

object Main extends App {
  val playerNames = Seq("PlayerA", "PlayerB")

  playerNames.map(new PlayerRunner(_)).par.foreach(_.playGames(Nil))
}

class PlayerRunner(playerName: String) {

  /////////////////   CONFIG   //////////////////
  val kdomApiUrl = "http://localhost/" // Replace with actual kdom host
  val maxNumberOfOngoingGames: Int = 5
  // Only join these game ids (if None: join any game)
  val gameIdsToJoin: Option[Set[String]] = None
  //val gameIdsToJoin: Option[Set[String]] = Some(Set("d411c211-5658-437e-a2f8-7782dbc73a5f"))
  ///////////////// END CONFIG //////////////////

  val logger: Logger = LoggerFactory.getLogger(getClass)
  val client = new KdomClient(kdomApiUrl)
  val pollTimeMs: Int = 1000

  def playGames(myGames: Seq[MyGame]): Unit = {
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

    val myUnfinishedGames: Seq[MyGame] =
      myGames.filterNot(myGame => finishedGames.exists(_.uuid == myGame.gameId))

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

    // Games that can be joined by me:
    val gamesThatCanBeJoined: Seq[NewGame] = {
      for {
        newGame   <- client.getNewGames().get.newGames
        if gameIdsToJoin.forall(_.contains(newGame.uuid))
        if !newGame.joinedPlayers.exists(_.name == playerName)
      } yield {
        newGame
      }
    }

    // Look for games to join:
    val joinedGames: Seq[MyGame] = {
      for {
        newGame         <- gamesThatCanBeJoined.take(maxNumberOfOngoingGames - myGames.size)
        gameId           = newGame.uuid
        playerWithToken <- client.postJoinGame(gameId, playerName)
      } yield {
        MyGame(gameId, playerWithToken)
      }
    }

    sleep()
    playGames(myUnfinishedGames ++ joinedGames)
  }

  def sleep() = Thread.sleep(pollTimeMs)
}

case class MyGame(gameId: String, playerWithToken: PlayerWithToken)
