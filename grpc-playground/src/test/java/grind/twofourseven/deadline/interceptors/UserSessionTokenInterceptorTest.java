package grind.twofourseven.deadline.interceptors;

import grind.twofourseven.common.GrpcServer;
import grind.twofourseven.common.ResponseObserver;
import grind.twofourseven.deadline.AbstractInterceptorTest;
import grind.twofourseven.deadline.DeadlineBankService;
import grind.twofourseven.deadline.interceptor.UserTokenInterceptor;
import grind.twofourseven.model.deadline.BalanceCheckRequest;
import grind.twofourseven.model.deadline.Money;
import grind.twofourseven.model.deadline.WithdrawRequest;
import io.grpc.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

class UserSessionTokenInterceptorTest extends AbstractInterceptorTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserSessionTokenInterceptorTest.class);
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
                builder.addService(new DeadlineBankService())
                        .intercept(new UserTokenInterceptor())
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
            var response = bankServiceBlockingStub.withCallCredentials(new UserSessionToken("user-token-1"))
                    .getAccountBalance(request);
            LOGGER.info("{}", response);
        });
    }

    @Test
    void asyncDeadlineTestSuccess() {
        var request = WithdrawRequest.newBuilder().setAccountNumber(1)
                .setAmount(50).build();
        var responseObserver = ResponseObserver.<Money>create();
        bankServiceAsyncStub.withDeadline(Deadline.after(2, TimeUnit.SECONDS))
                .withCallCredentials(new UserSessionToken("user-token-1"))
                .withdraw(request, responseObserver);
        responseObserver.await();
        Assertions.assertEquals(Status.Code.DEADLINE_EXCEEDED,
                Status.fromThrowable(responseObserver.getThrowable()).getCode());
    }

    @Test
    void asyncDeadlineTestFail() {
        var request = WithdrawRequest.newBuilder().setAccountNumber(1)
                .setAmount(50).build();
        var responseObserver = ResponseObserver.<Money>create();
        bankServiceAsyncStub.withDeadline(Deadline.after(2, TimeUnit.SECONDS))
                .withCallCredentials(new UserSessionToken("user-token-3"))
                .withdraw(request, responseObserver);
        responseObserver.await();
        Assertions.assertEquals(Status.Code.PERMISSION_DENIED,
                Status.fromThrowable(responseObserver.getThrowable()).getCode());
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
