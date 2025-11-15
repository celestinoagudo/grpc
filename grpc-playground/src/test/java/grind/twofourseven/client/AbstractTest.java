package grind.twofourseven.client;

import grind.twofourseven.common.AbstractChannelTest;
import grind.twofourseven.common.GrpcServer;
import grind.twofourseven.model.unary.BankServiceGrpc;
import grind.twofourseven.model.unary.TransferServiceGrpc;
import grind.twofourseven.patterns.service.BankService;
import grind.twofourseven.patterns.service.TransferService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public abstract class AbstractTest extends AbstractChannelTest {

    private final GrpcServer grpcServer = GrpcServer.create(new BankService(), new TransferService());
    protected BankServiceGrpc.BankServiceBlockingStub bankServiceBlockingStub;
    protected BankServiceGrpc.BankServiceStub bankServiceAsyncStub;
    protected TransferServiceGrpc.TransferServiceStub transferServiceAsyncStub;

    @BeforeEach
    public void setup() {
        grpcServer.start();
        bankServiceBlockingStub = BankServiceGrpc.newBlockingStub(channel);
        bankServiceAsyncStub = BankServiceGrpc.newStub(channel);
        transferServiceAsyncStub = TransferServiceGrpc.newStub(channel);
    }

    @AfterEach
    public void stop() {
        grpcServer.stop();
    }

}