package se.apogo.kdomai.client

import io.shaka.http.Http.http
import io.shaka.http.Request.{GET, POST}
import io.shaka.http.Response
import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.slf4j.{Logger, LoggerFactory}

object KdomClient {
  val logger: Logger = LoggerFactory.getLogger(getClass)
}
class KdomClient(apiBaseUrl: String) {
  require(apiBaseUrl.endsWith("/"))

  implicit val formats = DefaultFormats

  private def path(elem: String*): String = {
    elem.mkString("/")
  }

  def get(endpoint: String): Response = {
    val url = apiBaseUrl + endpoint
    val response = http(GET(apiBaseUrl + endpoint))
    KdomClient.logger.info(s"GET ${url}: ${response.status.code} ${response.status.description}")
    response
  }

  def post(endpoint: String): Response = {
    val url = apiBaseUrl + endpoint
    val response = http(POST(apiBaseUrl + endpoint))
    KdomClient.logger.info(s"POST ${url}: ${response.status.code} ${response.status.description}")
    response
  }

  def getJson(endpoint: String): Option[JValue] = {
    parseOpt(get(endpoint).entityAsString)
  }

  def getAs[T](endpoint: String)(implicit mf: scala.reflect.Manifest[T]): Option[T] = {
    getJson(endpoint).flatMap(_.extractOpt[T])
  }

  def postJsonResponse(endpoint: String): Option[JValue] = {
    parseOpt(post(endpoint).entityAsString)
  }

  def postResponseAs[T](endpoint: String)(implicit mf: scala.reflect.Manifest[T]): Option[T] = {
    postJsonResponse(endpoint).flatMap(_.extractOpt[T])
  }

  def getNewGames(): Option[NewGames] = {
    getAs[NewGames]("new-games/")
  }

  def postNewGame(playerCount: Int): Option[NewGame] = {
    require(2 <= playerCount && playerCount <= 4)
    postResponseAs[NewGame](s"new-games/?playerCount=${playerCount}")
  }

  def getGame(gameId: String): Option[Game] = {
    getAs[Game](path("games", gameId))
  }

  def getGames(): Option[BriefGames] = {
    getAs[BriefGames]("games/")
  }

  def postNewGame(): Option[NewGame] = {
    postResponseAs[NewGame]("new-games/")
  }

  def postJoinGame(gameId: String, playerName: String): Option[PlayerWithToken] = {
    postResponseAs[PlayerWithToken](path("new-games", gameId, "join", playerName))
  }

  def getAvailableMoves(gameId: String): Option[Moves] = {
    getAs[Moves](path("games", gameId, "available-moves"))
  }

  def postMove(gameId: String, playerId: String, moveNumber: Int): Option[Game] = {
    postResponseAs[Game](path("games", gameId, "players", playerId, "moves", moveNumber.toString))
  }
}
