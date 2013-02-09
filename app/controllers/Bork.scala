package controllers

import play.api._
import play.api.mvc._

object Bork extends Controller {
  def bork = Action {
    Ok("It works!")
  }
  def beurk = Action {
    Ok("It works! x3")
  }
}

object Zoubah extends Controller {
  def miam = Action {
    Ok("It works! x2")
  }
}
