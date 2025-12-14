package grind.twofourseven.deadline;

import grind.twofourseven.common.GrpcServer;
import grind.twofourseven.deadline.interceptor.GzipResponseInterceptor;
import grind.twofourseven.model.deadline.BankServiceGrpc;
import io.grpc.ClientInterceptor;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;

import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class AbstractInterceptorTest {
    private GrpcServer grpcServer;
    protected BankServiceGrpc.BankServiceBlockingStub bankServiceBlockingStub;
    protected BankServiceGrpc.BankServiceStub bankServiceAsyncStub;
    protected ManagedChannel managedChannel;

    protected abstract List<ClientInterceptor> getClientInterceptors();

    protected GrpcServer createServer() {
        return GrpcServer.create(6565, builder ->
                builder.addService(new DeadlineBankService())
                        .intercept(new GzipResponseInterceptor())
        );
    }

    @BeforeAll
    public void setup() {
        grpcServer = createServer();
        grpcServer.start();
        managedChannel = ManagedChannelBuilder.forAddress("localhost", 6565)
                .usePlaintext().intercept(getClientInterceptors()).build();
        bankServiceBlockingStub = BankServiceGrpc.newBlockingStub(managedChannel);
        bankServiceAsyncStub = BankServiceGrpc.newStub(managedChannel);

    }

    @AfterAll
    public void stop() {
        grpcServer.stop();
        managedChannel.shutdownNow();
    }
}
