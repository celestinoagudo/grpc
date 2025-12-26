package com.grind.two.four.seven.grpc.service;

import com.grind.two.four.seven.grpc.service.handler.StockTradeRequestHandler;
import com.grind.two.four.seven.grpc.service.handler.UserInformationRequestHandler;
import com.grind.two.four.seven.grpc.user.*;
import io.grpc.stub.StreamObserver;
import org.springframework.grpc.server.service.GrpcService;

@GrpcService //automatically adds service to the GRPC server
public class UserService extends UserServiceGrpc.UserServiceImplBase {

    private final UserInformationRequestHandler userInformationRequestHandler;
    private final StockTradeRequestHandler stockTradeRequestHandler;

    public UserService(final UserInformationRequestHandler userInformationRequestHandler,
                       final StockTradeRequestHandler stockTradeRequestHandler) {
        this.userInformationRequestHandler = userInformationRequestHandler;
        this.stockTradeRequestHandler = stockTradeRequestHandler;
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
        final var stockTradeResponse = TradeAction.SELL.equals(request.getAction())
                ? stockTradeRequestHandler.buyStock(request)
                : stockTradeRequestHandler.sellStock(request);
        responseObserver.onNext(stockTradeResponse);
        responseObserver.onCompleted();

    }
}
