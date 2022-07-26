package com.github.AsadaGuitar.websocket

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory

import scala.concurrent.{Await, ExecutionContextExecutor, Future}
import scala.concurrent.duration.DurationInt
import scala.io.StdIn
import scala.language.postfixOps


object ChatApp extends App {

  val logger = LoggerFactory.getLogger(getClass)

  val config = ConfigFactory.load()
  val host = config.getString("websocket-server.host")
  val port = config.getInt("websocket-server.port")

  implicit val system: ActorSystem = ActorSystem("chatApp")
  implicit val ec: ExecutionContextExecutor = system.dispatcher
  implicit val timeout: Timeout = Timeout(5 seconds)

  val router =
    path(Segment) { roomId =>
      logger.info(roomId)
      pathEndOrSingleSlash {
        parameter("username") { username =>
          logger.info(username)
          handleWebSocketMessages(ChatRoomService.chatRoomStream(roomId, username))
        }
      }
    }

  val bindingFuture = Http().newServerAt(host, port).bind(router)
  logger.info(s"Server online at http://${host}:${port}/")

  StdIn.readLine()
  bindingFuture.flatMap( _ => system.terminate())
}
