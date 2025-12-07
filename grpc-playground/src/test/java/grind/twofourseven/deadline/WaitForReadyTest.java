package grind.twofourseven.deadline;

import com.google.common.util.concurrent.Uninterruptibles;
import grind.twofourseven.common.AbstractChannelTest;
import grind.twofourseven.common.GrpcServer;
import grind.twofourseven.model.deadline.BankServiceGrpc;
import grind.twofourseven.model.deadline.Money;
import grind.twofourseven.model.deadline.WithdrawRequest;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

class WaitForReadyTest extends AbstractChannelTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(WaitForReadyTest.class);
    private final GrpcServer grpcServer = GrpcServer.create(new DeadlineBankService());
    private BankServiceGrpc.BankServiceBlockingStub bankServiceBlockingStub;
//    protected BankServiceGrpc.BankServiceStub bankServiceAsyncStub;

    @BeforeAll
    void setup() {
        Runnable startServerAfter5Seconds = () -> {
            LOGGER.info("Starting server after 5 seconds....");
            Uninterruptibles.sleepUninterruptibly(Duration.ofSeconds(5));
            grpcServer.start();
        };
        bankServiceBlockingStub = BankServiceGrpc.newBlockingStub(channel);
        Thread.ofVirtual().start(startServerAfter5Seconds);
//        bankServiceAsyncStub = BankServiceGrpc.newStub(channel);
    }

    @AfterAll
    void stop() {
        grpcServer.stop();
    }

    @Test
    void blockingDeadlineTest() {
        LOGGER.info("Sending request...");
        final var exception = Assertions.assertThrows(StatusRuntimeException.class, () -> {
            var request = WithdrawRequest.newBuilder().setAccountNumber(1)
                    .setAmount(50).build();
            var iterator = bankServiceBlockingStub
                    .withWaitForReady()
//                    .withDeadline(Deadline.after(2, TimeUnit.SECONDS))
                    .withdraw(request);
            while (iterator.hasNext()) {
                final Money next = iterator.next();
                LOGGER.info("{}", next);
            }
        });
//        Assertions.assertEquals(Status.Code.DEADLINE_EXCEEDED, exception.getStatus().getCode());
    }
}
