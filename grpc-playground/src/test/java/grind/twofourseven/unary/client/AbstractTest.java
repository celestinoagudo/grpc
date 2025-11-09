package grind.twofourseven.unary.client;

import grind.twofourseven.common.AbstractChannelTest;
import grind.twofourseven.common.GrpcServer;
import grind.twofourseven.model.unary.BankServiceGrpc;
import grind.twofourseven.unary.service.BankService;
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