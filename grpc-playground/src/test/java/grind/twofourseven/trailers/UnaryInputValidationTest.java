package grind.twofourseven.trailers;

import grind.twofourseven.common.ResponseObserver;
import grind.twofourseven.model.input.trailer.AccountBalance;
import grind.twofourseven.model.input.trailer.BalanceCheckRequest;
import grind.twofourseven.model.input.trailer.ErrorMessage;
import grind.twofourseven.model.input.trailer.ValidationCode;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.protobuf.ProtoUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

class UnaryInputValidationTest extends AbstractTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(UnaryInputValidationTest.class);

    @Test
    void blockingInputValidationTest() {
        var request = BalanceCheckRequest.newBuilder().setAccountNumber(11).build();
        var exception = assertThrows(StatusRuntimeException.class, () -> bankServiceBlockingStub.getAccountBalance(request));
        var key = ProtoUtils.keyForProto(ErrorMessage.getDefaultInstance());
        var metadata = Status.trailersFromThrowable(exception);
        LOGGER.info("Validation Code: {}", metadata.get(key));
        assertEquals(ValidationCode.INVALID_ACCOUNT, getvalidationCode(exception));
    }

    @Test
    void nonBlockingValidationTest() {
        var request = BalanceCheckRequest.newBuilder().setAccountNumber(11).build();
        var responseObserver = ResponseObserver.<AccountBalance>create();
        bankServiceAsyncStub.getAccountBalance(request, responseObserver);
        responseObserver.await();
        assertTrue(responseObserver.getItems().isEmpty());
        assertNotNull(responseObserver.getThrowable());
        assertEquals(ValidationCode.INVALID_ACCOUNT, getvalidationCode(responseObserver.getThrowable()));
    }
}
