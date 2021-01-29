package com.trek.user.service;

import com.trek.cache.UserRedisCommands;
import com.trek.user.models.User.UserModel;
import com.trek.user.models.User.UserRequest;
import com.trek.user.models.User.UserResponse;
import com.trek.user.models.UserServiceGrpc.UserServiceImplBase;
import io.grpc.Status;
import io.grpc.health.v1.HealthCheckResponse;
import io.grpc.stub.ServerCallStreamObserver;
import io.grpc.stub.StreamObserver;
import io.lettuce.core.codec.ByteArrayCodec;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

public class UserServiceImpl extends UserServiceImplBase {

  Logger logger = Logger.getLogger(UserServiceImpl.class.getName());

  private HealthCheckResponse.ServingStatus serviceStatus;

  private UserRedisCommands commands;

  private UserServiceImpl() {
    throw new UnsupportedOperationException("Not allowed");
  }

  public UserServiceImpl(UserRedisCommands commands) {
    this.commands = commands;
  }

  @Override
  public StreamObserver<UserRequest> search(StreamObserver<UserResponse> responseObserver) {
    return super.search(responseObserver);
  }

  @Override
  public void create(UserRequest request, StreamObserver<UserResponse> userResponseObserver) {

    if (!isRequestValid(request)) {
      userResponseObserver.onError(
          Status.INVALID_ARGUMENT.withDescription("Invalid Request received").asException());
    }

    UserModel userModel = request.getUser();
    ServerCallStreamObserver<UserResponse> responseObserver =
        (ServerCallStreamObserver<UserResponse>) userResponseObserver;

    logger.info("Request received on server: " + request.toString());

    try {

      byte[] resp = this.commands
          .saveUser(userModel.getId().getBytes(), userModel.toByteArray());

      String responseOk = new String(resp, StandardCharsets.UTF_8);

      if (responseOk.equalsIgnoreCase("ok")) {
        responseObserver
            .onNext(this.prepareResponse(new String(resp, StandardCharsets.UTF_8), userModel));
        responseObserver.onCompleted();
      } else {
        userResponseObserver.onError(
            Status.ABORTED.withDescription("The Data wasn't persisted").asException());
      }


    } catch (Exception ex) {
      userResponseObserver.onError(
          Status.ABORTED.withDescription("The Data wasn't persisted").asException());
    }

  }

  @Override
  public void update(UserRequest request, StreamObserver<UserResponse> responseObserver) {
  }

  @Override
  public void delete(UserRequest request, StreamObserver<UserResponse> responseObserver) {
  }

  @Override
  public void deactivate(UserRequest request, StreamObserver<UserResponse> responseObserver) {
  }

  private UserResponse prepareResponse(String status, UserModel userModel) {
    UserModel respModel = UserModel.newBuilder()
        .setFName(userModel.getFName())
        .setLName(userModel.getLName())
        .setIsActive(Boolean.TRUE)
        .setId(userModel.getId()).build();

    return UserResponse.newBuilder()
        .setUser(respModel)
        .setStatus(status)
        .build();
  }

  private void changeStatus(HealthCheckResponse.ServingStatus statusToBeUpdated) {
    this.serviceStatus = statusToBeUpdated;
  }

  private boolean isRequestValid(UserRequest userRequest) {

    if (null == userRequest && null == userRequest.getUser()) {
      return false;
    } else if (null == userRequest.getUser().getFName() || null == userRequest.getUser()
        .getLName()) {
      return false;
    } else {
      return true;
    }

  }

  public void setCommands(UserRedisCommands commands) {
    this.commands = commands;
  }

}
