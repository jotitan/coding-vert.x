package io.vertx.starter;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.MessageConsumer;

public class ReadMessageVerticle extends AbstractVerticle {

  @Override
  public void start(Future<Void> startFuture) throws Exception {
    MessageConsumer<String> consumer = vertx.eventBus().consumer("message.multicast");
    consumer.handler(message->{
      System.out.println("Received : "  + message.body());
    });
    startFuture.complete();
  }
}
