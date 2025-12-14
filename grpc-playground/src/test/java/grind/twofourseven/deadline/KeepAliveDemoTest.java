package grind.twofourseven.deadline;

import com.google.common.util.concurrent.Uninterruptibles;
import grind.twofourseven.common.GrpcServer;
import grind.twofourseven.model.deadline.BalanceCheckRequest;
import grind.twofourseven.model.deadline.BankServiceGrpc;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

class KeepAliveDemoTest extends AbstractChannelTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(KeepAliveDemoTest.class);
    private final GrpcServer grpcServer = GrpcServer.create(new DeadlineBankService());
    private BankServiceGrpc.BankServiceBlockingStub blockingStub;

    @BeforeAll
    void setup() {
        grpcServer.start();
        blockingStub = BankServiceGrpc.newBlockingStub(managedChannel);
    }

    @Test
    void lazyChannelDemo() {
        var request = BalanceCheckRequest.newBuilder().setAccountNumber(1).build();
        var response = blockingStub.getAccountBalance(request);
        LOGGER.info("{}", response);
        Uninterruptibles.sleepUninterruptibly(Duration.ofSeconds(30));
    }

    @AfterAll
    void stop() {
        grpcServer.stop();
    }
}
