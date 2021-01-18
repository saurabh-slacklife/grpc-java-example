package com.trek.user.service;

import com.trek.user.models.User.UserModel;
import com.trek.user.models.User.UserRequest;
import com.trek.user.models.User.UserResponse;
import com.trek.user.models.UserServiceGrpc.UserServiceImplBase;
import io.grpc.stub.ServerCallStreamObserver;
import io.grpc.stub.StreamObserver;

import java.util.logging.Logger;

public class UserServiceImpl extends UserServiceImplBase {

    Logger logger = Logger.getLogger(UserServiceImpl.class.getName());

    @Override
    public void search(UserRequest request, StreamObserver<UserResponse> responseObserver) {
    }

    @Override
    public void create(UserRequest request, StreamObserver<UserResponse> userResponseObserver) {
        UserModel userModel = request.getUser();
        ServerCallStreamObserver<UserResponse> responseObserver =
                (ServerCallStreamObserver<UserResponse>) userResponseObserver;

        logger.info("Request received on server: " + request.toString());

        UserModel respModel = UserModel.newBuilder()
                .setFName(userModel.getFName())
                .setLName(userModel.getLName())
                .setIsActive(Boolean.TRUE)
                .setId(userModel.getId()).build();

        UserResponse resp = UserResponse.newBuilder().setUser(respModel).build();


        responseObserver.onNext(resp);
        responseObserver.onCompleted();
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

}
