package com.grind.two.four.seven.grpc.aggregator.service;

import com.grind.two.four.seven.grpc.user.UserInformation;
import com.grind.two.four.seven.grpc.user.UserInformationRequest;
import com.grind.two.four.seven.grpc.user.UserServiceGrpc;
import org.springframework.stereotype.Service;

@Service
public class UserRestService {

    private final UserServiceGrpc.UserServiceBlockingStub userClient;

    public UserRestService(final UserServiceGrpc.UserServiceBlockingStub userClient) {
        this.userClient = userClient;
    }

    public UserInformation getUserInformation(final int userId) {
        var request = UserInformationRequest.newBuilder().setUserId(userId).build();
        return userClient.getUserInformation(request);
    }

}
