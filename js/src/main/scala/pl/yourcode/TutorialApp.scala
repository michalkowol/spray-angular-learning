package pl.yourcode

import scala.scalajs.js.JSApp

object TutorialApp extends JSApp {
  def main(): Unit = {
    println(List(1, 2, 3).map(_ + 8).map(_ * 11))
  }
}
