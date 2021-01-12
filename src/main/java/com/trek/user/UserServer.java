package com.trek.user;

import com.trek.user.service.UserServiceImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class UserServer {

    private static final Logger logger = Logger.getLogger(UserServer.class.getName());

    private Server server;

    public UserServer(int port) {
        this.server = ServerBuilder.forPort(port).addService(new UserServiceImpl())
                .build();
    }

    public static void main(String[] args) throws Exception {
        UserServer userServer = new UserServer(1313);
        userServer.start();
        userServer.blockUntilShutdown();
    }

    private void start() throws IOException {
        this.server.start();
        logger.info("Server started, listening on" + 1313);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    UserServer.this.stop();
                    logger.info("Server interrupted, shutting down");
                } catch (InterruptedException e) {
                    e.printStackTrace(System.err);
                }
            }
        });
    }

    private void stop() throws InterruptedException {
        if (this.server != null) {
            logger.info("Shutting down");
            this.server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    private void blockUntilShutdown() throws InterruptedException {
        if (this.server != null) {
            this.server.awaitTermination();
        }
    }
}
