package se.apogo.kdomai.client

import org.json4s.DefaultFormats
import org.testng.Assert
import org.testng.annotations.Test

class KdomClientTest {
  def client = new KdomClient("http://kdom.mratin.se/")

  @Test
  def test_getGames(): Unit = {

    implicit val formats = DefaultFormats
    val a = client.getJson("games/").flatMap(_.extractOpt[BriefGames])

    val result = client.getGames()

    Assert.assertTrue(result.isDefined)
  }
}
