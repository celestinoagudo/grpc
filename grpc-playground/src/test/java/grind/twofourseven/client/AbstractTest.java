package grind.twofourseven.client;

import grind.twofourseven.common.AbstractChannelTest;
import grind.twofourseven.common.GrpcServer;
import grind.twofourseven.model.unary.BankServiceGrpc;
import grind.twofourseven.patterns.service.BankService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public abstract class AbstractTest extends AbstractChannelTest {

    private final GrpcServer grpcServer = GrpcServer.create(new BankService());
    protected BankServiceGrpc.BankServiceBlockingStub blockingStub;
    protected BankServiceGrpc.BankServiceStub asyncStub;

    @BeforeEach
    public void setup() {
        grpcServer.start();
        blockingStub = BankServiceGrpc.newBlockingStub(channel);
        asyncStub = BankServiceGrpc.newStub(channel);
    }

    @AfterEach
    public void stop() {
        grpcServer.stop();
    }

}