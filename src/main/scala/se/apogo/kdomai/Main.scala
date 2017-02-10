package se.apogo.kdomai

import se.apogo.kdomai.client.KdomClient

object Main extends App {
  val kdomApiUrl = "http://localhost:8080/"

  val client = new KdomClient(kdomApiUrl)
}
