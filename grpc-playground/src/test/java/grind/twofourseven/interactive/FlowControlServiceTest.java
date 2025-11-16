package grind.twofourseven.interactive;

import grind.twofourseven.common.AbstractChannelTest;
import grind.twofourseven.common.GrpcServer;
import grind.twofourseven.model.interactive.FlowControlServiceGrpc;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FlowControlServiceTest extends AbstractChannelTest {

    private final GrpcServer server = GrpcServer.create(new FlowControlService());
    private FlowControlServiceGrpc.FlowControlServiceStub flowControlServiceAsyncStub;

    @BeforeAll
    void setup() {
        server.start();
        flowControlServiceAsyncStub = FlowControlServiceGrpc.newStub(channel);
    }

    @Test
    void flowControlTest() {
        var responseObserver = new ResponseHandler();
        var requestObserver = flowControlServiceAsyncStub.getMessages(responseObserver);
        responseObserver.setRequestSizeStreamObserver(requestObserver);
        responseObserver.start();
        responseObserver.await();
    }

    @AfterAll
    void stop() {
        server.stop();
    }
}