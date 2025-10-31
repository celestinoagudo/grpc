package grind.twofourseven.unary.client;

import grind.twofourseven.model.unary.BalanceCheckRequest;
import grind.twofourseven.model.unary.BankServiceGrpc;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GrpcClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(GrpcClient.class);

    public static void main(String[] args) {
        var channel = ManagedChannelBuilder.forAddress("localhost", 6565).usePlaintext().build();
        var stub = BankServiceGrpc.newBlockingStub(channel);
        var balance = stub.getAccountBalance(BalanceCheckRequest.newBuilder().setAccountNumber(2).build());
        LOGGER.info("{}", balance);
    }

}
