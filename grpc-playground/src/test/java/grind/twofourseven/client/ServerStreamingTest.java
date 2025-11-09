package grind.twofourseven.client;

import grind.twofourseven.common.ResponseObserver;
import grind.twofourseven.model.unary.Money;
import grind.twofourseven.model.unary.WithdrawRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ServerStreamingTest extends AbstractTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerStreamingTest.class);

    @Test
    void blockingClientWithdrawTest() {
        var request = WithdrawRequest.newBuilder().setAmount(20).setAccountNumber(1)
                .build();
        var iterator = blockingStub.withdraw(request);
        var count = 0;
        while (iterator.hasNext()) {
            LOGGER.info("Received Money: {}", iterator.next());
            ++count;
        }
        Assertions.assertEquals(2, count);
    }

    @Test
    void asyncClientWithdrawTest() {
        var request = WithdrawRequest.newBuilder().setAmount(20).setAccountNumber(1)
                .build();
        var observer = ResponseObserver.<Money>create();
        asyncStub.withdraw(request, observer);
        observer.await();
        Assertions.assertEquals(2, observer.getItems().size());
        Assertions.assertEquals(10, observer.getItems().getFirst().getAmount());
        Assertions.assertNull(observer.getThrowable());
    }
}
