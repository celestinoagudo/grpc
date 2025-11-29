package grind.twofourseven.validation;

import grind.twofourseven.common.ResponseObserver;
import grind.twofourseven.model.input.validation.Money;
import grind.twofourseven.model.input.validation.WithdrawRequest;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ServerStreamingValidationTest extends AbstractTest {

    @ParameterizedTest
    @MethodSource("testData")
    void blockingInputValidationTest(final WithdrawRequest request, final Status.Code code) {
        var exception = assertThrows(StatusRuntimeException.class, () -> bankServiceBlockingStub.withdraw(request).hasNext());
        assertEquals(code, exception.getStatus().getCode());
    }

    @ParameterizedTest
    @MethodSource("testData")
    void nonBlockingValidationTest(final WithdrawRequest request, final Status.Code code) {
        var responseObserver = ResponseObserver.<Money>create();
        bankServiceAsyncStub.withdraw(request, responseObserver);
        responseObserver.await();
        assertTrue(responseObserver.getItems().isEmpty());
        assertNotNull(responseObserver.getThrowable());
        assertEquals(code, ((StatusRuntimeException) responseObserver.getThrowable()).getStatus().getCode());
    }

    private Stream<Arguments> testData() {
        return Stream.of(
                Arguments.of(WithdrawRequest.newBuilder().setAccountNumber(11).setAmount(10).build(), Status.Code.INVALID_ARGUMENT),
                Arguments.of(WithdrawRequest.newBuilder().setAccountNumber(1).setAmount(17).build(), Status.Code.INVALID_ARGUMENT),
                Arguments.of(WithdrawRequest.newBuilder().setAccountNumber(1).setAmount(120).build(), Status.Code.FAILED_PRECONDITION)
        );
    }
}
