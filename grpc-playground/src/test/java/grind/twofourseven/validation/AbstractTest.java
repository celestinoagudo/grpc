package grind.twofourseven.validation;

import grind.twofourseven.common.AbstractChannelTest;
import grind.twofourseven.common.GrpcServer;
import grind.twofourseven.input.validation.service.BankService;
import grind.twofourseven.model.input.validation.BankServiceGrpc;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public abstract class AbstractTest extends AbstractChannelTest {

    private final GrpcServer grpcServer = GrpcServer.create(new BankService());
    protected BankServiceGrpc.BankServiceBlockingStub bankServiceBlockingStub;
    protected BankServiceGrpc.BankServiceStub bankServiceAsyncStub;

    @BeforeEach
    public void setup() {
        grpcServer.start();
        bankServiceBlockingStub = BankServiceGrpc.newBlockingStub(channel);
        bankServiceAsyncStub = BankServiceGrpc.newStub(channel);
    }

    @AfterEach
    public void stop() {
        grpcServer.stop();
    }

}