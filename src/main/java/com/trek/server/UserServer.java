package com.trek.server;

import com.trek.cache.UserRedisCommands;
import com.trek.dao.RedisServiceFactory;
import com.trek.user.service.UserServiceImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.health.v1.HealthCheckResponse;
import io.grpc.services.HealthStatusManager;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class UserServer {

  private static final Logger logger = Logger.getLogger(UserServer.class.getName());

  private Server server;
  private HealthStatusManager healthManager;
  private int serverPort;

  public UserServer(int serverPort) {
    this.serverPort = serverPort;
  }

  public static void main(String[] args) throws Exception {

    Properties props = new Properties();

    if (args.length > 0) {
      try {
        FileInputStream defaultStream = new FileInputStream(args[0]);
        props.load(defaultStream);
      } catch (IOException ex) {
        System.out.println(ex.getMessage());
        Runtime.getRuntime().exit(1);
      }
    } else {
      System.out.println("Please pass input file path for Properties file");
      Runtime.getRuntime().exit(1);
    }

    int serverPort = Integer.parseInt(props.getProperty("server-port", "1313"));

    UserServer userServer = new UserServer(serverPort);
    userServer.initializeServer(props);
    userServer.start();
    userServer.blockUntilShutdown();
  }

  private void initializeServer(Properties props) {
    UserRedisCommands redisUserCommands = this.getUserRedisCommands(props);

    UserServiceImpl userService = new UserServiceImpl(redisUserCommands);

    this.healthManager = new HealthStatusManager();

    this.server = ServerBuilder.forPort(this.serverPort)
        .addService(userService)
        .addService(this.healthManager.getHealthService())
        .build();
  }

  private UserRedisCommands getUserRedisCommands(Properties props) {
    String redisHost = props.getProperty("redis-host", "redis-server");
    int redisPort = Integer.parseInt(props.getProperty("redis-port", "6379"));
    int redisDatabase = Integer.parseInt(props.getProperty("redis-db", "0"));

    RedisServiceFactory userRedisFactory = new RedisServiceFactory(redisHost, redisPort,
        redisDatabase);

    UserRedisCommands redisUserCommands = userRedisFactory
        .getStringByteCodecFactory()
        .getCommands(UserRedisCommands.class);
    return redisUserCommands;
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
