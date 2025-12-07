package grind.twofourseven.deadline;

import grind.twofourseven.common.ResponseObserver;
import grind.twofourseven.model.deadline.AccountBalance;
import grind.twofourseven.model.deadline.BalanceCheckRequest;
import io.grpc.Deadline;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

class UnaryDeadlineTest extends AbstractTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(UnaryDeadlineTest.class);

    @Test
    void blockingDeadlineTest() {
        var request = BalanceCheckRequest.newBuilder().setAccountNumber(1).build();
        final var exception = Assertions.assertThrows(StatusRuntimeException.class, () ->
                bankServiceBlockingStub.withDeadline(Deadline.after(2, TimeUnit.SECONDS))
                        .getAccountBalance(request)
        );
        Assertions.assertEquals(Status.Code.DEADLINE_EXCEEDED, exception.getStatus().getCode());
    }

    @Test
    void asyncDeadlineTest() {
        var request = BalanceCheckRequest.newBuilder().setAccountNumber(1).build();
        var responseObserver = ResponseObserver.<AccountBalance>create();
        bankServiceAsyncStub.withDeadline(Deadline.after(3, TimeUnit.SECONDS))
                .getAccountBalance(request, responseObserver);
        responseObserver.await();
        Assertions.assertEquals(Status.Code.DEADLINE_EXCEEDED,
                Status.fromThrowable(responseObserver.getThrowable()).getCode());
    }

}
