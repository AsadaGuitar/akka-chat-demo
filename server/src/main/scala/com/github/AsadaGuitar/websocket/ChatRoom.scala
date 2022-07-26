package com.github.AsadaGuitar.websocket

import akka.stream.UniqueKillSwitch
import akka.stream.scaladsl.Flow


final case class ChatRoom(roomId: String, bus: Flow[ChatMessage, ChatMessage, UniqueKillSwitch])
