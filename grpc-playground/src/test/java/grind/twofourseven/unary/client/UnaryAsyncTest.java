package grind.twofourseven.unary.client;

import grind.twofourseven.model.unary.AccountBalance;
import grind.twofourseven.model.unary.BalanceCheckRequest;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

class UnaryAsyncTest extends AbstractTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(UnaryAsyncTest.class);

    @Test
    void getBalanceTest() throws InterruptedException {
        var request = BalanceCheckRequest.newBuilder().setAccountNumber(1).build();
        var latch = new CountDownLatch(1);
        asyncStub.getAccountBalance(request, new StreamObserver<>() {
            @Override
            public void onNext(final AccountBalance accountBalance) {
                LOGGER.info("Async balance received: {}", accountBalance);
                try {
                    Assertions.assertEquals(100, accountBalance.getBalance());
                } finally {
                    latch.countDown();
                }
            }

            @Override
            public void onError(final Throwable throwable) {
                LOGGER.error("Account Balance Retrieval failed due to: {}", throwable.getMessage());
            }

            @Override
            public void onCompleted() {
                LOGGER.info("Completed!");
            }
        });
        latch.await();
    }
}
