package grind.twofourseven.patterns.service;

import grind.twofourseven.model.unary.TransferRequest;
import grind.twofourseven.model.unary.TransferResponse;
import grind.twofourseven.model.unary.TransferServiceGrpc;
import grind.twofourseven.patterns.request.handlers.TransferRequestHandler;
import io.grpc.stub.StreamObserver;

public class TransferService extends TransferServiceGrpc.TransferServiceImplBase {
    @Override
    public StreamObserver<TransferRequest> transfer(final StreamObserver<TransferResponse> responseObserver) {
        return new TransferRequestHandler(responseObserver);
    }
}
