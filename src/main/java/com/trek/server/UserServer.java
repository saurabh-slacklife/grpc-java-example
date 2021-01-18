package com.trek.server;

import com.trek.cache.UserRedisCommands;
import com.trek.user.service.UserServiceImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.health.v1.HealthCheckResponse;
import io.grpc.services.HealthStatusManager;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.dynamic.RedisCommandFactory;
import io.lettuce.core.resource.DefaultClientResources;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class UserServer {

  private static final Logger logger = Logger.getLogger(UserServer.class.getName());

  private Server server;
  private HealthStatusManager healthManager;
  private UserRedisCommands commands;

  public UserServer(int serverPort) {
    this.healthManager = new HealthStatusManager();
    this.server = ServerBuilder.forPort(serverPort)
        .addService(new UserServiceImpl(this.commands))
        .addService(this.healthManager.getHealthService())
        .build();
  }

  public static void main(String[] args) throws Exception {

    UserServer userServer = new UserServer(1313);
    userServer.initializeRedisClient("localhost", 6379, 0);
    userServer.start();
    userServer.blockUntilShutdown();

  }

  private void initializeRedisClient(String host, int port, int database) {
    RedisURI redisURI = RedisURI.Builder.redis(host, port).withDatabase(database).build();
    RedisClient redisClient = RedisClient
        .create(DefaultClientResources.builder().build(), redisURI);
    RedisCommandFactory redisFactory = new RedisCommandFactory(redisClient.connect());
    this.commands = redisFactory.getCommands(UserRedisCommands.class);
  }

  private void start() throws IOException {
    this.server.start();
    logger.info("Server started, listening on" + this.server.getPort());
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        try {
          UserServer.this.stop();
          logger.info("Server interrupted, shutting down");
        } catch (InterruptedException e) {
          healthManager.setStatus("", HealthCheckResponse.ServingStatus.NOT_SERVING);
          e.printStackTrace(System.err);
        }
      }
    });

    this.healthManager.setStatus("UserServiceImpl", HealthCheckResponse.ServingStatus.SERVING);
    this.healthManager.setStatus("", HealthCheckResponse.ServingStatus.SERVING);
  }

  private void stop() throws InterruptedException {
    if (this.server != null) {
      logger.info("Shutting down");
      this.healthManager.clearStatus("");
      this.healthManager.clearStatus("UserServiceImpl");
      this.server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
    }
  }

  private void blockUntilShutdown() throws InterruptedException {
    if (this.server != null) {
      this.server.awaitTermination();
    }
  }
}
