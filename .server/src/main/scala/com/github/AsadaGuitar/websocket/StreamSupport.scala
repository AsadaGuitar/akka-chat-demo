package com.github.AsadaGuitar.websocket

import akka.Done
import akka.actor.{ActorRef, ActorSystem, Props}
import akka.stream.{CompletionStrategy, OverflowStrategy}
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}


object StreamSupport {

  def abstractActorFlow[IN,OUT](create: ActorRef => Props)(implicit classicalSystem: ActorSystem): Flow[IN,OUT,_] = {

    val (outRef, pub) =
      Source.actorRef(
        completionMatcher = { case Done => CompletionStrategy.immediately },
        failureMatcher = PartialFunction.empty,
        bufferSize = 100,
        overflowStrategy = OverflowStrategy.dropHead
      ).toMat(Sink.asPublisher(false))(Keep.both).run()

    Flow.fromSinkAndSource(
      Sink.actorRef(
        classicalSystem.actorOf(create(outRef)),
        onCompleteMessage = "",
        onFailureMessage = (_: Throwable) => ""
      ),
      Source.fromPublisher(pub)
    )
  }
}
