package grind.twofourseven.trailers;

import grind.twofourseven.common.ResponseObserver;
import grind.twofourseven.model.input.trailer.Money;
import grind.twofourseven.model.input.trailer.ValidationCode;
import grind.twofourseven.model.input.trailer.WithdrawRequest;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ServerStreamingValidationTest extends AbstractTest {

    @ParameterizedTest
    @MethodSource("testData")
    void blockingInputValidationTest(final WithdrawRequest request, final ValidationCode validationCode) {
        var exception = assertThrows(StatusRuntimeException.class,
                () -> bankServiceBlockingStub.withdraw(request).hasNext());
        assertEquals(validationCode, getvalidationCode(exception));
    }

    @ParameterizedTest
    @MethodSource("testData")
    void nonBlockingValidationTest(final WithdrawRequest request, final ValidationCode validationCode) {
        var responseObserver = ResponseObserver.<Money>create();
        bankServiceAsyncStub.withdraw(request, responseObserver);
        responseObserver.await();
        assertTrue(responseObserver.getItems().isEmpty());
        assertNotNull(responseObserver.getThrowable());
        assertEquals(validationCode, getvalidationCode(responseObserver.getThrowable()));
    }

    private Stream<Arguments> testData() {
        return Stream.of(
                Arguments.of(WithdrawRequest.newBuilder().setAccountNumber(11).setAmount(10).build(),
                        ValidationCode.INVALID_ACCOUNT),
                Arguments.of(WithdrawRequest.newBuilder().setAccountNumber(1).setAmount(17).build(),
                        ValidationCode.INVALID_AMOUNT),
                Arguments.of(WithdrawRequest.newBuilder().setAccountNumber(1).setAmount(120).build(),
                        ValidationCode.INVALID_AMOUNT)
        );
    }
}
