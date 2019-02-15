package io.vertx.starter;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.ext.web.Router;


public class MainVerticle extends AbstractVerticle {

  @Override
  public void start(Future<Void> startFuture) throws Exception {
    Router router = Router.router(vertx);
    router.get("/hello").handler(routing->{
      routing.response().end("Hello world");
    });

    router.post("/message").handler(routing->{
      routing.request().bodyHandler(body->{
        System.out.println(body.toString());
        pushMessageOnEventHandler(body.toString());
        routing.response().end(String.valueOf((body.toString().length())));
      });
    });


    vertx.createHttpServer().requestHandler(router::accept).listen(8080, http -> {
      if (http.succeeded()) {
        startFuture.complete();
        System.out.println("HTTP server started on http://localhost:8080");
      } else {
        startFuture.fail(http.cause());
      }
    });
  }

  private void pushMessageOnEventHandler(String message){
    vertx.eventBus().publish("message.multicast",message);
  }

}


