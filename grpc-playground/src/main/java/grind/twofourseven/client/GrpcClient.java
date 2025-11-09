package grind.twofourseven.client;

import grind.twofourseven.model.unary.AccountBalance;
import grind.twofourseven.model.unary.BalanceCheckRequest;
import grind.twofourseven.model.unary.BankServiceGrpc;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public class GrpcClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(GrpcClient.class);

    public static void main(String[] args) throws InterruptedException {
        var channel = ManagedChannelBuilder.forAddress("localhost", 6565).usePlaintext().build();
        var stub = BankServiceGrpc.newBlockingStub(channel);
        var balance = stub.getAccountBalance(BalanceCheckRequest.newBuilder().setAccountNumber(2).build());
        LOGGER.info("{}", balance);

        //async stub
        var asyncStub = BankServiceGrpc.newStub(channel);
        asyncStub.getAccountBalance(BalanceCheckRequest.newBuilder().setAccountNumber(2).build(),
                new StreamObserver<>() {
                    @Override
                    public void onNext(final AccountBalance accountBalance) {
                        LOGGER.info("ACCOUNT BALANCE: {}", accountBalance);
                    }

                    @Override
                    public void onError(final Throwable throwable) {

                    }

                    @Override
                    public void onCompleted() {
                        LOGGER.info("COMPLETED!");
                    }
                });

        LOGGER.info("DONE!");
        Thread.sleep(Duration.ofSeconds(1));
    }

}
