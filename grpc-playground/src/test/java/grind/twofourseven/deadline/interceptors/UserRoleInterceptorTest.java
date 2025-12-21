package grind.twofourseven.deadline.interceptors;

import grind.twofourseven.common.GrpcServer;
import grind.twofourseven.deadline.AbstractInterceptorTest;
import grind.twofourseven.deadline.UserRoleDeadlineBankService;
import grind.twofourseven.deadline.interceptor.UserRoleInterceptor;
import grind.twofourseven.model.deadline.BalanceCheckRequest;
import io.grpc.CallCredentials;
import io.grpc.ClientInterceptor;
import io.grpc.Metadata;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;

class UserRoleInterceptorTest extends AbstractInterceptorTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserRoleInterceptorTest.class);
    private static final Metadata.Key<String> USER_TOKEN_KEY =
            Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER);
    private static final String BEARER = "Bearer";

    @Override
    protected List<ClientInterceptor> getClientInterceptors() {
        return Collections.emptyList();
    }


    @Override
    protected GrpcServer createServer() {
        return GrpcServer.create(6565, builder ->
                builder.addService(new UserRoleDeadlineBankService())
                        .intercept(new UserRoleInterceptor())
        );
    }

    @Test
    void blockingDeadlineTestError() {
        var request = BalanceCheckRequest.newBuilder().setAccountNumber(1).build();
        Assertions.assertThrows(StatusRuntimeException.class, () -> {
            var response = bankServiceBlockingStub.withCallCredentials(new UserSessionToken("test-jwt"))
                    .getAccountBalance(request);
            LOGGER.info("{}", response);
        });
    }

    @Test
    void blockingDeadlineTestSuccess() {
        var request = BalanceCheckRequest.newBuilder().setAccountNumber(1).build();
        Assertions.assertDoesNotThrow(() -> {
            var firstAcctBalanceCheck = bankServiceBlockingStub.withCallCredentials(new UserSessionToken("user-token-3"))
                    .getAccountBalance(request);
            LOGGER.info("First: {}", firstAcctBalanceCheck);
            var secondAcctBalanceCheck = bankServiceBlockingStub.withCallCredentials(new UserSessionToken("user-token-3"))
                    .getAccountBalance(request);
            LOGGER.info("Second: {}", secondAcctBalanceCheck);
        });
    }

    private static class UserSessionToken extends CallCredentials {

        private final String jwt;

        public UserSessionToken(final String jwt) {
            this.jwt = jwt;
        }

        @Override
        public void applyRequestMetadata(final RequestInfo requestInfo,
                                         final Executor executor,
                                         final MetadataApplier metadataApplier) {
            executor.execute(() -> { //useful by any chance that we're calling external service to get the jwt.
                var metadata = new Metadata();
                metadata.put(USER_TOKEN_KEY, "%s %s".formatted(BEARER, jwt));
                metadataApplier.apply(metadata);
            });

        }
    }

}
