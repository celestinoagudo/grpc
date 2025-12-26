package com.grind.two.four.seven.grpc.service;

import com.grind.two.four.seven.grpc.service.handler.UserInformationRequestHandler;
import com.grind.two.four.seven.grpc.user.*;
import io.grpc.stub.StreamObserver;
import org.springframework.grpc.server.service.GrpcService;

@GrpcService //automatically adds service to the GRPC server
public class UserService extends UserServiceGrpc.UserServiceImplBase {

    private final UserInformationRequestHandler userInformationRequestHandler;

    public UserService(final UserInformationRequestHandler userInformationRequestHandler) {
        this.userInformationRequestHandler = userInformationRequestHandler;
    }

    @Override
    public void getUserInformation(final UserInformationRequest request,
                                   final StreamObserver<UserInformation> responseObserver) {
        var userInformation = userInformationRequestHandler.getUserInformation(request);
        responseObserver.onNext(userInformation);
        responseObserver.onCompleted();
    }

    @Override
    public void tradeStock(final StockTradeRequest request,
                           final StreamObserver<StockTradeResponse> responseObserver) {

    }
}
