package com.github.AsadaGuitar.websocket


object ChatRoomRepository {

  private var repository: Vector[ChatRoom] = Vector.empty

  def findById(roomId: String): Option[ChatRoom] = repository.find(_.roomId equals roomId)

  def insertOrUpdate(chatroom: ChatRoom): Unit = repository = repository :+ chatroom
}
