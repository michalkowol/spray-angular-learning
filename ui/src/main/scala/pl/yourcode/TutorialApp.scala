package pl.yourcode

import org.scalajs.dom
import org.scalajs.dom.ext.Ajax
import scala.concurrent.ExecutionContext.Implicits.global

import scala.scalajs.js

object TutorialApp extends js.JSApp {
  def main(): Unit = {
    dom.document.getElementById("start").innerHTML = List(1, 2, 8).map(_ + 8).map(_ * 71).toString()
    Ajax.get("http://api.openweathermap.org/data/2.5/weather?q=Gliwice").map { xhr =>
      val text = xhr.responseText
      val e = js.JSON.parse(text)
      val lat = e.coord.lat
      val lon = e.coord.lon
      dom.console.log(lat)
      dom.console.log(lon)
      dom.document.getElementById("lat").innerHTML = lat.toString()
      dom.document.getElementById("lon").innerHTML = lon.toString()
    }
  }
}
