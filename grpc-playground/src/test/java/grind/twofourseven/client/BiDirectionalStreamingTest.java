package grind.twofourseven.client;

import grind.twofourseven.common.ResponseObserver;
import grind.twofourseven.model.unary.TransferRequest;
import grind.twofourseven.model.unary.TransferResponse;
import grind.twofourseven.model.unary.TransferStatus;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BiDirectionalStreamingTest extends AbstractTest {

    @Test
    void transferTest() {
        var responseObserver = ResponseObserver.<TransferResponse>create();
        var requestObserver = transferServiceAsyncStub.transfer(responseObserver);
        var requests = List.of(
                TransferRequest.newBuilder()
                        .setAmount(10).setFromAccount(6)
                        .setToAccount(6).build(),
                TransferRequest.newBuilder()
                        .setAmount(110).setFromAccount(6)
                        .setToAccount(7).build(),
                TransferRequest.newBuilder()
                        .setAmount(10).setFromAccount(6)
                        .setToAccount(7).build(),
                TransferRequest.newBuilder()
                        .setAmount(10).setFromAccount(7)
                        .setToAccount(6).build()
        );
        requests.forEach(requestObserver::onNext);
        requestObserver.onCompleted();
        responseObserver.await();
        assertEquals(4, responseObserver.getItems().size());
        validate(responseObserver.getItems().getFirst(), TransferStatus.REJECTED, 100, 100);
        validate(responseObserver.getItems().get(1), TransferStatus.REJECTED, 100, 100);
        validate(responseObserver.getItems().get(2), TransferStatus.COMPLETED, 90, 110);
        validate(responseObserver.getItems().get(3), TransferStatus.COMPLETED, 100, 100);
    }

    private void validate(final TransferResponse response, final TransferStatus status,
                          final int fromAccountBalance, final int toAccountBalance) {
        assertEquals(status, response.getStatus());
        assertEquals(fromAccountBalance, response.getFromAccount().getBalance());
        assertEquals(toAccountBalance, response.getToAccount().getBalance());
    }
}
