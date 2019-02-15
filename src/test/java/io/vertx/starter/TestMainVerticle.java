package io.vertx.starter;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.RequestOptions;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.Timeout;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(VertxExtension.class)
public class TestMainVerticle {

  @BeforeEach
  void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
    vertx.deployVerticle(new MainVerticle(), testContext.succeeding(id -> testContext.completeNow()));
    vertx.deployVerticle(new ReadMessageVerticle(), testContext.succeeding(id -> testContext.completeNow()));
  }

  @Test
  @DisplayName("Should start a Web Server with hello on port 8080")
  @Timeout(value = 10, timeUnit = TimeUnit.SECONDS)
  void start_http_hello_world(Vertx vertx, VertxTestContext testContext) throws Throwable {
    vertx.createHttpClient().getNow(8080, "localhost", "/hello", response -> testContext.verify(() -> {
      assertTrue(response.statusCode() == 200);
      response.handler(body -> {
        assertTrue(body.toString().contains("Hello world"));
        testContext.completeNow();
      });
    }));
  }

  @Test
  @DisplayName("Should post a message at /message")
  @Timeout(value = 10, timeUnit = TimeUnit.SECONDS)
  void start_http_post_message(Vertx vertx, VertxTestContext testContext) throws Throwable {
    WebClient client = WebClient.create(vertx);
    client.post(8080, "localhost", "/message")
      .sendBuffer(Buffer.buffer(TEST_MESSAGE),
        ar->testContext.verify(() -> {
          assertTrue(ar.succeeded());
          assertEquals("12",ar.result().body().toString());
          testContext.completeNow();
        }));
  }

  private final static String TEST_MESSAGE = "bonjour toto";

  @Test
  @DisplayName("Should post a message at /message")
  @Timeout(value = 10, timeUnit = TimeUnit.SECONDS)
  void start_http_post_message_part2(Vertx vertx, VertxTestContext testContext) throws Throwable {
    WebClient client = WebClient.create(vertx);
    client.post(8080, "localhost", "/message")
      .sendBuffer(Buffer.buffer(TEST_MESSAGE),
        ar->testContext.verify(() -> {
          vertx.eventBus().consumer("message.multicast").handler(message->{
              assertEquals(TEST_MESSAGE,message.body());
          });
          testContext.completeNow();
        }));
  }

}
