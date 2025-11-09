package grind.twofourseven.client;

import grind.twofourseven.common.ResponseObserver;
import grind.twofourseven.model.unary.AccountBalance;
import grind.twofourseven.model.unary.DepositRequest;
import grind.twofourseven.model.unary.Money;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

class ClientStreamingTest extends AbstractTest {

    @Test
    void depositTest() {
        //incoming response from the server.
        var responseObserver = ResponseObserver.<AccountBalance>create();
        //outgoing request from the client.
        var requestObserver = asyncStub.deposit(responseObserver);

        //initial message - account number
        requestObserver.onNext(DepositRequest.newBuilder().setAccountNumber(5).build());
        //emit 10 items.
        IntStream.rangeClosed(1, 10).mapToObj(_ -> Money.newBuilder().setAmount(10))
                .map(money -> DepositRequest.newBuilder().setMoney(money).build())
                .forEach(requestObserver::onNext);
        //notifying the server that we're done.
        requestObserver.onCompleted();

        //at this point out response observer should receive a response.
        responseObserver.await();
        Assertions.assertEquals(1, responseObserver.getItems().size());
        Assertions.assertEquals(200, responseObserver.getItems().getFirst().getBalance());
        Assertions.assertNull(responseObserver.getThrowable());
    }
}
