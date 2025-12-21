package grind.twofourseven.deadline.interceptors;

import grind.twofourseven.common.GrpcServer;
import grind.twofourseven.deadline.AbstractInterceptorTest;
import grind.twofourseven.deadline.DeadlineBankService;
import grind.twofourseven.deadline.interceptor.ApiKeyValidationInterceptor;
import grind.twofourseven.model.deadline.BalanceCheckRequest;
import io.grpc.ClientInterceptor;
import io.grpc.Metadata;
import io.grpc.stub.MetadataUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

class ApiKeyInterceptorTest extends AbstractInterceptorTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiKeyInterceptorTest.class);
    private static final Metadata.Key<String> API_KEY = Metadata.Key.of("api-key", Metadata.ASCII_STRING_MARSHALLER);

    @Override
    protected List<ClientInterceptor> getClientInterceptors() {
        return List.of(MetadataUtils.newAttachHeadersInterceptor(getApiKey()));
    }

    @Override
    protected GrpcServer createServer() {
        return GrpcServer.create(6565, builder ->
                builder.addService(new DeadlineBankService())
                        .intercept(new ApiKeyValidationInterceptor())
        );
    }

    @Test
    void blockingDeadlineTest() {
        var request = BalanceCheckRequest.newBuilder().setAccountNumber(1).build();
        Assertions.assertDoesNotThrow(() -> {
            var response = bankServiceBlockingStub.getAccountBalance(request);
            LOGGER.info("{}", response);
        });
    }

    private Metadata getApiKey() {
        var metadata = new Metadata();
        metadata.put(API_KEY, "bank-client-secret");
        return metadata;
    }
}
