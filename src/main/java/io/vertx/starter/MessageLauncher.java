package io.vertx.starter;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;

public class MessageLauncher {

  public static void main(String[] args) {
    System.out.println("Le java c'est bien");
    clusterMode();

  }

  private static void clusterMode(){
    final ClusterManager mgr = new HazelcastClusterManager();
    final VertxOptions options = new VertxOptions().setClusterManager(mgr);
    Vertx.clusteredVertx(options, res -> {
      if (res.succeeded()) {
        System.out.println("res ok shop");
        final Vertx vertx = res.result();
        staticMode(vertx);
      } else {
        System.out.println("FAIL !!!");
      }
    });

    System.out.println("End of Shop Launcher");
  }

  private static void staticMode(Vertx vertx){
    vertx.deployVerticle(MainVerticle.class.getName(),new DeploymentOptions().setInstances(1));
    vertx.deployVerticle(ReadMessageVerticle.class.getName(),new DeploymentOptions().setInstances(1));
  }
}
