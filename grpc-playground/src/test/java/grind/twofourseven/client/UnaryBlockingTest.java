package grind.twofourseven.client;

import com.google.protobuf.Empty;
import grind.twofourseven.model.unary.BalanceCheckRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class UnaryBlockingTest extends AbstractTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(UnaryBlockingTest.class);

    @Test
    void getBalanceTest() {
        var request = BalanceCheckRequest.newBuilder().setAccountNumber(2).build();
        var balance = blockingStub.getAccountBalance(request);
        LOGGER.info("Unary Balanced Received: {}", balance);
        Assertions.assertEquals(100, balance.getBalance());
    }

    @Test
    void getAllAccountsTest() {
        var allAccounts = blockingStub.getAllAccounts(Empty.getDefaultInstance());
        LOGGER.info("All Accounts: {}", allAccounts);
        Assertions.assertFalse(allAccounts.getAccountsList().isEmpty());
    }
}
