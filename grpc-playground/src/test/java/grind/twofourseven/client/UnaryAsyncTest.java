package grind.twofourseven.client;

import com.google.protobuf.Empty;
import grind.twofourseven.common.ResponseObserver;
import grind.twofourseven.model.unary.AccountBalance;
import grind.twofourseven.model.unary.AllAccountsResponse;
import grind.twofourseven.model.unary.BalanceCheckRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class UnaryAsyncTest extends AbstractTest {

    @Test
    void getBalanceTest() {
        var request = BalanceCheckRequest.newBuilder().setAccountNumber(1).build();
        var observer = ResponseObserver.<AccountBalance>create();
        bankServiceAsyncStub.getAccountBalance(request, observer);
        observer.await();
        Assertions.assertEquals(1, observer.getItems().size());
        Assertions.assertEquals(100, observer.getItems().getFirst().getBalance());
        Assertions.assertNull(observer.getThrowable());
    }

    @Test
    void allAccountsTest() {
        var observer = ResponseObserver.<AllAccountsResponse>create();
        bankServiceAsyncStub.getAllAccounts(Empty.getDefaultInstance(), observer);
        observer.await();
        Assertions.assertEquals(1, observer.getItems().size());
        Assertions.assertEquals(10, observer.getItems().getFirst().getAccountsCount());
        Assertions.assertNull(observer.getThrowable());
    }
}
