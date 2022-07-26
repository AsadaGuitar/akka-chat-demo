package com.github.AsadaGuitar.websocket

import akka.actor.{Actor, ActorRef, PoisonPill, Props}
import akka.http.scaladsl.model.ws.TextMessage


object ResponseActor {
  def props(username: String, ref: ActorRef): Props = Props(new ResponseActor(username, ref))
}

class ResponseActor(username: String, ref: ActorRef) extends Actor {
  override def receive: Receive = {
    case Talk(text) =>
      ref ! TextMessage(text)

    case Join(joinUser) =>
      ref ! TextMessage(s"join: $joinUser")

    case Leave =>
      ref ! PoisonPill
      self ! PoisonPill
  }
}


