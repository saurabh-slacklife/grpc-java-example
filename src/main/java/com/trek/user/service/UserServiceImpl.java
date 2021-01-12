package com.trek.user.service;

import com.trek.user.models.User;
import com.trek.user.models.UserServiceGrpc;
import io.grpc.stub.StreamObserver;

import java.util.logging.Logger;

public class UserServiceImpl extends UserServiceGrpc.UserServiceImplBase{

    Logger logger = Logger.getLogger(UserServiceImpl.class.getName());

    @Override
    public void search(User.UserRequest request, StreamObserver<User.UserResponse> responseObserver) {
    }

    @Override
    public void create(User.UserRequest request, StreamObserver<User.UserResponse> responseObserver) {
        User.UserModel userModel = request.getUser();

        User.UserModel respModel = User.UserModel.newBuilder()
                .setFName(userModel.getFName())
                .setLName(userModel.getLName())
                .setIsActive(Boolean.TRUE)
                .setId(userModel.getId()).build();

        User.UserResponse resp = User.UserResponse.newBuilder().setUser(respModel).build();

        responseObserver.onNext(resp);
        responseObserver.onCompleted();
    }

    @Override
    public void update(User.UserRequest request, StreamObserver<User.UserResponse> responseObserver) {
    }

    @Override
    public void delete(User.UserRequest request, StreamObserver<User.UserResponse> responseObserver) {
    }

    @Override
    public void deactivate(User.UserRequest request, StreamObserver<User.UserResponse> responseObserver) {
    }

}
