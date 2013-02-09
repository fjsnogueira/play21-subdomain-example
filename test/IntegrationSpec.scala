package test

import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._

/**
 * add your integration spec here.
 * An integration test will fire up a whole play application in a real (or headless) browser
 */
class IntegrationSpec extends Specification {
  
  val all_domains = List("me", "localhost", "verylonglocalhostname", "foo.example.local", "bar.example.local")
  val port = 3333

  "Application" should {
    
    "give access to the assets to everybody" in {
      running(TestServer(port), HTMLUNIT) { checkroute(_, all_domains, "jQuery v1.9.0", "/assets/javascripts/jquery-1.9.0.min.js") }
    }
    "display the default play content only on 'me'" in {
      running(TestServer(port), HTMLUNIT) { checkroute(_, List("me"), "Your new application is ready.") }
    }
    "say plop to my subdomain (argument)" in {
      running(TestServer(port), HTMLUNIT) { b =>
        checkroute(b, List("foo.example.local"), "Plop foo =)", "/plop")
        checkroute(b, List("bar.example.local"), "Plop bar =)", "/plop")
      }
    }
    //"be able to force https" in {
    //  running(TestServer(port), HTMLUNIT) { checkroute(_, List("me"), "Hello world!"), "/hello/world", true }
    //  running(TestServer(port), HTMLUNIT) { checkroute(_, List("me"), "Action not found"), "/hello/world", false }
    //}
    "be able to set a subdomain to an included route file" in {
      running(TestServer(port), HTMLUNIT) { checkroute(_, List("verylonglocalhostname"), "It works!") }
    }
    "have the subdomain before each route or include" in {
      running(TestServer(port), HTMLUNIT) { checkroute(_, all_domains, "It works! x2", "/zoubah/yummy") }
    }
    
  }

  def checkroute (browser: TestBrowser, domains: List[String], txt: String, path: String = "/", secure: Boolean = false) = {
    val protocol = if (secure) "https" else "http"

    domains.foreach { domain =>
      browser.goTo(protocol+"://"+domain+":"+port+path)
      browser.pageSource must contain(txt)
    }
    
    all_domains.diff(domains).foreach { domain =>
      browser.goTo(protocol+"://"+domain+":"+port+path)
      browser.pageSource must not contain(txt)//contain("Action not found")
    }
  }
  
}

/*
[me]
GET     /                           controllers.Application.index
GET     /assets/*file               controllers.Assets.at(path="/public", file)
[:id.example.local]
GET     /plop                       controllers.Application.plop(id)
SGET    /hello/*world               controllers.Application.hello(world)
[verylonglocalhostname]
->  /                               borkbork.Routes
->  /bork                           borkbork.Routes

GET     /                           controllers.Bork.bork
GET     /yummy                      controllers.Bork.miam
GET     /dohh                       controllers.Bork.beurk

*/*/*/