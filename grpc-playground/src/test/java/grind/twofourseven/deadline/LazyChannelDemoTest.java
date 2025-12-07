package grind.twofourseven.deadline;

import grind.twofourseven.common.GrpcServer;
import grind.twofourseven.model.deadline.BalanceCheckRequest;
import grind.twofourseven.model.deadline.BankServiceGrpc;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LazyChannelDemoTest extends AbstractChannelTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(LazyChannelDemoTest.class);
    private final GrpcServer grpcServer = GrpcServer.create(new DeadlineBankService());
    private BankServiceGrpc.BankServiceBlockingStub bankServiceBlockingStub;

    @BeforeAll
    public void setup() {
        grpcServer.start();
        bankServiceBlockingStub = BankServiceGrpc.newBlockingStub(managedChannel);
    }

    @AfterAll
    public void stop() {
        grpcServer.stop();
    }

    @Test
    public void lazyChannelDemo() {
        var request = BalanceCheckRequest.newBuilder().setAccountNumber(1).build();
        //connection is created on request.
        var response = bankServiceBlockingStub.getAccountBalance(request);
        LOGGER.info("{}", response);
    }
}
