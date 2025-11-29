package grind.twofourseven.validation;

import grind.twofourseven.common.ResponseObserver;
import grind.twofourseven.model.input.validation.AccountBalance;
import grind.twofourseven.model.input.validation.BalanceCheckRequest;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UnaryInputValidationTest extends AbstractTest {

    @Test
    void blockingInputValidationTest() {
        var request = BalanceCheckRequest.newBuilder().setAccountNumber(11).build();
        var exception = assertThrows(StatusRuntimeException.class, () -> bankServiceBlockingStub.getAccountBalance(request));
        assertEquals(Status.INVALID_ARGUMENT.getCode(), exception.getStatus().getCode());
    }

    @Test
    void nonBlockingValidationTest() {
        var request = BalanceCheckRequest.newBuilder().setAccountNumber(11).build();
        var responseObserver = ResponseObserver.<AccountBalance>create();
        bankServiceAsyncStub.getAccountBalance(request, responseObserver);
        responseObserver.await();
        assertTrue(responseObserver.getItems().isEmpty());
        assertNotNull(responseObserver.getThrowable());
        assertEquals(Status.INVALID_ARGUMENT.getCode(),
                ((StatusRuntimeException) responseObserver.getThrowable()).getStatus().getCode());
    }
}
