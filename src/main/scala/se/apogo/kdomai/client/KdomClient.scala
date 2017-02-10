package se.apogo.kdomai.client

import io.shaka.http.Http.http
import io.shaka.http.Request.{GET, POST}
import io.shaka.http.Response
import org.json4s._
import org.json4s.jackson.JsonMethods._

class KdomClient(apiBaseUrl: String) {
  require(apiBaseUrl.endsWith("/"))

  implicit val formats = DefaultFormats

  def path(elem: String*): String = {
    elem.mkString("/")
  }

  def get(endpoint: String): Response = {
    http(GET(apiBaseUrl + endpoint))
  }

  def post(endpoint: String): Response = {
    http(POST(apiBaseUrl + endpoint))
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
    getAs("new-games/")
  }

  def getGames(): Option[BriefGames] = {
    getAs[BriefGames]("games/")
  }

  def postNewGame(): Option[NewGame] = {
    postResponseAs("new-games/")
  }

  def getAvailableMoves(uuid: String): Option[Moves] = {
    getAs(path("games", uuid, "available-moves"))
  }
}
