package grind.twofourseven.deadline.interceptors;

import grind.twofourseven.common.ResponseObserver;
import grind.twofourseven.deadline.AbstractInterceptorTest;
import grind.twofourseven.deadline.interceptor.DeadlineInterceptor;
import grind.twofourseven.model.deadline.AccountBalance;
import grind.twofourseven.model.deadline.BalanceCheckRequest;
import io.grpc.ClientInterceptor;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;

class DeadlineInterceptorTest extends AbstractInterceptorTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeadlineInterceptorTest.class);

    @Override
    protected List<ClientInterceptor> getClientInterceptors() {
        return List.of(new DeadlineInterceptor(Duration.ofSeconds(2)));
    }


    @Test
    void blockingDeadlineTest() {
        var request = BalanceCheckRequest.newBuilder().setAccountNumber(1).build();
        final var exception = Assertions.assertThrows(StatusRuntimeException.class, () ->
                bankServiceBlockingStub.getAccountBalance(request)
        );
        Assertions.assertEquals(Status.Code.DEADLINE_EXCEEDED, exception.getStatus().getCode());
    }

    @Test
    void asyncDeadlineTest() {
        var request = BalanceCheckRequest.newBuilder().setAccountNumber(1).build();
        var responseObserver = ResponseObserver.<AccountBalance>create();
        bankServiceAsyncStub
                .getAccountBalance(request, responseObserver);
        responseObserver.await();
        Assertions.assertEquals(Status.Code.DEADLINE_EXCEEDED,
                Status.fromThrowable(responseObserver.getThrowable()).getCode());
    }
}
