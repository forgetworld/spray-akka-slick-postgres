package mezmer.actors

import akka.actor._
import akka.io.IO
import spray.can.Http
import spray.http._
import HttpMethods._
import MediaTypes._
import akka.routing.RoundRobinRouter
import com.typesafe.config.ConfigFactory

import mezmer.server.Server

object Starter {
  case object Start
}

class Starter extends Actor {
  import Starter.Start

  val config = ConfigFactory.load()
  val (mainInterface: String, mainPort: Int) = (
    config.getString("app.interface"),
    config.getInt("app.port")
  )

  implicit val system = context.system

  def receive: Receive = {
    case Start =>
      val handler: ActorRef =
        context.actorOf(Props[Server].withRouter(RoundRobinRouter(nrOfInstances = 10)))
      IO(Http) ! Http.Bind(handler, interface = mainInterface, port = mainPort)
  }
}