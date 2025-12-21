package grind.twofourseven.deadline.interceptor;

import io.grpc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Set;

public class UserTokenInterceptor implements ServerInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserTokenInterceptor.class);

    private static final Metadata.Key<String> USER_TOKEN_KEY =
            Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER);
    private static final Set<String> PRIME_SET = Set.of("user-token-1", "user-token-2");
    private static final Set<String> STANDARD_SET = Set.of("user-token-3", "user-token-4");
    public static final String BEARER = "Bearer";

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(final ServerCall<ReqT, RespT> serverCall, final Metadata metadata, final ServerCallHandler<ReqT, RespT> serverCallHandler) {
        var token = extractToken(metadata.get(USER_TOKEN_KEY));
        LOGGER.info("Token: {}", token);
        if (!isValid(token))
            return close(serverCall, metadata, Status.UNAUTHENTICATED.withDescription("Token is either null or invalid!"));
        var isOneMessage = serverCall.getMethodDescriptor().getType().serverSendsOneMessage();
        LOGGER.info("Is One Message? : {}", isOneMessage);
        if (isOneMessage || PRIME_SET.contains(token)) {
            return serverCallHandler.startCall(serverCall, metadata);
        }
        return close(serverCall, metadata, Status.PERMISSION_DENIED.withDescription("User is not allowed to perform this operation!"));
    }

    private String extractToken(final String value) {
        LOGGER.info("value: {}", value);
        return Objects.nonNull(value) && value.startsWith(BEARER) ? value.substring(BEARER.length()).trim() : null;
    }

    private boolean isValid(final String token) {
        return Objects.nonNull(token) && (PRIME_SET.contains(token) || STANDARD_SET.contains(token));
    }

    private <ReqT, RespT> ServerCall.Listener<ReqT> close(final ServerCall<ReqT, RespT> serverCall,
                                                          final Metadata metadata,
                                                          final Status status) {
        serverCall.close(status, metadata);
        return new ServerCall.Listener<ReqT>() {
        };
    }
}
