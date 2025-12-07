package grind.twofourseven.deadline;

import grind.twofourseven.common.ResponseObserver;
import grind.twofourseven.model.deadline.Money;
import grind.twofourseven.model.deadline.WithdrawRequest;
import io.grpc.Deadline;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

class StreamDeadlineTest extends AbstractTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(StreamDeadlineTest.class);

    @Test
    void blockingDeadlineTest() {
        final var exception = Assertions.assertThrows(StatusRuntimeException.class, () -> {
            var request = WithdrawRequest.newBuilder().setAccountNumber(1)
                    .setAmount(50).build();
            var iterator = bankServiceBlockingStub
                    .withDeadline(Deadline.after(2, TimeUnit.SECONDS))
                    .withdraw(request);
            while (iterator.hasNext()) {
                iterator.next();
            }
        });
        Assertions.assertEquals(Status.Code.DEADLINE_EXCEEDED, exception.getStatus().getCode());
    }

    @Test
    void asyncDeadlineTest() {
        var request = WithdrawRequest.newBuilder().setAccountNumber(1)
                .setAmount(50).build();
        var responseObserver = ResponseObserver.<Money>create();
        bankServiceAsyncStub.withDeadline(Deadline.after(2, TimeUnit.SECONDS)).withdraw(request, responseObserver);
        responseObserver.await();
        Assertions.assertEquals(2, responseObserver.getItems().size());
        Assertions.assertEquals(Status.Code.DEADLINE_EXCEEDED, Status.fromThrowable(responseObserver.getThrowable()).getCode());
    }
}
