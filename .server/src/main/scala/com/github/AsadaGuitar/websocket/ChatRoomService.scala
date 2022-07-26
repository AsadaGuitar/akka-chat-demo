package com.github.AsadaGuitar.websocket

import akka.NotUsed
import akka.actor.ActorSystem
import akka.http.scaladsl.model.ws.Message
import akka.stream.{KillSwitches, Materializer}
import akka.stream.scaladsl.{BroadcastHub, Flow, Keep, MergeHub, Sink, Source}
import org.slf4j.LoggerFactory

import scala.concurrent.duration.DurationInt


object ChatRoomService {

  private val logger = LoggerFactory.getLogger(getClass)

  private def createChatRoom(roomId: String)(implicit system: ActorSystem, mat: Materializer): ChatRoom ={

    /**
     *  MergeHub と BroadcastHub を繋いだ Flow を実行し Source と Sink を取得する。
     */
    val (sink: Sink[ChatMessage, NotUsed], source: Source[ChatMessage, NotUsed]) =
      MergeHub.source[ChatMessage](perProducerBufferSize = 16)
        .toMat(BroadcastHub.sink(bufferSize = 256))(Keep.both)
        .run()

    /**
     * Subscriber が存在しない時チャンネルが流れないようにする。
     */
    source.runWith(Sink.ignore)

    /**
     * Flow.fromSinkAndSource を使用する事でユーザーは Sink と Source を定義する事を強制される。
     * BidiStage として KillSwitch を登録する事で 元の Sink と Source を同時に閉じる事が可能になる。
     * RunnableGraph.backpressureTimeout を定義する事で指定時間以上チャンネルをブロックした Subscriber を強制削除する。
     */
    val bus = Flow
      .fromSinkAndSource(sink, source)
      .joinMat(KillSwitches.singleBidi[ChatMessage, ChatMessage])(Keep.right)
      .backpressureTimeout(3.seconds)

    ChatRoom(roomId, bus)
  }


  def chatRoomStream(roomId: String, username: String)(implicit system: ActorSystem, mat: Materializer): Flow[Message, Message, _] ={

    val room = ChatRoomRepository.findById(roomId) match {
      case Some(chatRoom) => chatRoom
      case None =>
        val chatRoom = createChatRoom(roomId)
        logger.info(s"created room id=$roomId username=$username")
        ChatRoomRepository.insertOrUpdate(chatRoom)
        logger.debug(s"saved room.")
        chatRoom
    }

    val inputFlow  = StreamSupport.abstractActorFlow(RequestActor.props(username, _))
    val outputFlow = StreamSupport.abstractActorFlow(ResponseActor.props(username, _))

    inputFlow.viaMat(room.bus)(Keep.right).viaMat(outputFlow)(Keep.right)
  }
}
