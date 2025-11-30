package grind.twofourseven.trailers;

import grind.twofourseven.common.AbstractChannelTest;
import grind.twofourseven.common.GrpcServer;
import grind.twofourseven.input.trailer.service.BankService;
import grind.twofourseven.model.input.trailer.BankServiceGrpc;
import grind.twofourseven.model.input.trailer.ErrorMessage;
import grind.twofourseven.model.input.trailer.ValidationCode;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.protobuf.ProtoUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.util.Optional;

public abstract class AbstractTest extends AbstractChannelTest {

    private static final Metadata.Key<ErrorMessage> ERROR_MESSAGE_KEY = ProtoUtils.keyForProto(ErrorMessage.getDefaultInstance());

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

    protected ValidationCode getvalidationCode(final Throwable throwable) {
        return Optional.ofNullable(Status.trailersFromThrowable(throwable))
                .map(metadata -> metadata.get(ERROR_MESSAGE_KEY))
                .map(ErrorMessage::getValidationCode)
                .orElseThrow();
    }

}