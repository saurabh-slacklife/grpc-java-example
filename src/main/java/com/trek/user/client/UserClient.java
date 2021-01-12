package com.trek.user.client;

import com.trek.user.models.User;
import com.trek.user.models.UserServiceGrpc;
import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserClient {
    private static Logger logger = Logger.getLogger(UserClient.class.getName());

    private final UserServiceGrpc.UserServiceBlockingStub blockingStub;
    private final UserServiceGrpc.UserServiceStub asyncStub;

    public UserClient(Channel channel) {

        this.blockingStub = UserServiceGrpc.newBlockingStub(channel);
        this.asyncStub = UserServiceGrpc.newStub(channel);
    }

    private void createUser() {

        User.UserModel userModel = User.UserModel.newBuilder()
                .setFName("Saurabh")
                .setLName("Saxena")
                .setId("someid")
                .setIsActive(Boolean.FALSE)
                .build();

        logger.info("#### User Req Info ####");
        logger.info("User Req: " + userModel.toString());

        User.UserRequest request = User.UserRequest.newBuilder()
                .setUser(userModel).build();
        User.UserResponse userResponse;

        try {
            userResponse = this.blockingStub.create(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return;
        }

        logger.info("#### User Resp Info ####");
        logger.info("User Resp: " + userResponse.toString());

    }

    public static void main(String[] args) throws Exception {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 1313).usePlaintext().build();

        try {

            UserClient client = new UserClient(channel);
            client.createUser();
        } finally {
            channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
        }
    }

}
