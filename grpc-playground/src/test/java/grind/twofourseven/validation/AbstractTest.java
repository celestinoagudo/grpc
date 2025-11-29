package grind.twofourseven.validation;

import grind.twofourseven.common.AbstractChannelTest;
import grind.twofourseven.common.GrpcServer;
import grind.twofourseven.input.validation.service.BankService;
import grind.twofourseven.model.input.validation.BankServiceGrpc;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

public abstract class AbstractTest extends AbstractChannelTest {

    private final GrpcServer grpcServer = GrpcServer.create(new BankService());
    protected BankServiceGrpc.BankServiceBlockingStub bankServiceBlockingStub;
    protected BankServiceGrpc.BankServiceStub bankServiceAsyncStub;

    @BeforeAll
    public void setup() {
        grpcServer.start();
        bankServiceBlockingStub = BankServiceGrpc.newBlockingStub(channel);
        bankServiceAsyncStub = BankServiceGrpc.newStub(channel);
    }

    @AfterAll
    public void stop() {
        grpcServer.stop();
    }

}