package com.github.AsadaGuitar.websocket


sealed trait ChatMessage
final case class Join(username: String) extends ChatMessage
case object Leave extends ChatMessage
final case class Talk(text: String) extends ChatMessage