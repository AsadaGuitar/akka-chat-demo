package com.github.AsadaGuitar.websocket

import akka.actor.{Actor, ActorRef, PoisonPill, Props}
import akka.http.scaladsl.model.ws.TextMessage


object RequestActor {
  def props(username: String, ref: ActorRef): Props = Props(new RequestActor(username, ref))
}

class RequestActor(username: String, ref: ActorRef) extends Actor {
  override def receive: Receive = {
    case TextMessage.Strict(msg) =>
      context.system.log.info(s"Send Message By: $username.")
      ref ! Talk(s"$username: $msg")
  }

  override def preStart(): Unit = {
    context.system.log.info(s"Join User: $username.")
    ref ! Join(username)
  }

  override def postStop(): Unit = {
    context.system.log.info(s"Leave User: $username.")
    ref ! Leave
    ref ! PoisonPill
  }
}