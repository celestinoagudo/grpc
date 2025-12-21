package grind.twofourseven.deadline.interceptor;

import grind.twofourseven.deadline.UserRole;
import io.grpc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Set;

public class UserRoleInterceptor implements ServerInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserRoleInterceptor.class);
    private static final Context.Key<UserRole> USER_ROLE_KEY = Context.key("user-role");

    private static final Metadata.Key<String> USER_TOKEN_KEY =
            Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER);
    private static final Set<String> PRIME_SET = Set.of("user-token-1", "user-token-2");
    private static final Set<String> STANDARD_SET = Set.of("user-token-3", "user-token-4");
    public static final String BEARER = "Bearer";

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(final ServerCall<ReqT, RespT> serverCall, final Metadata metadata, final ServerCallHandler<ReqT, RespT> serverCallHandler) {

        var token = extractToken(metadata.get(USER_TOKEN_KEY));
        var context = toContext(token);
        LOGGER.info("Token: {}", token);
        if (Objects.nonNull(context)) {
            return Contexts.interceptCall(context, serverCall, metadata, serverCallHandler); //server call execution has to be done with the updated context.
        }

        return close(serverCall, metadata, Status.UNAUTHENTICATED.withDescription("User is not allowed to perform this operation!"));
    }

    private String extractToken(final String value) {
        LOGGER.info("value: {}", value);
        return Objects.nonNull(value) && value.startsWith(BEARER) ? value.substring(BEARER.length()).trim() : null;
    }

    private Context toContext(final String token) {
        if (Objects.nonNull(token) && (PRIME_SET.contains(token) || STANDARD_SET.contains(token))) {
            var role = PRIME_SET.contains(token) ? UserRole.PRIME : UserRole.STANDARD;
            LOGGER.info("Role Attached: {}", role.name());
            return Context.current().withValue(USER_ROLE_KEY, role);
        }
        return null;
    }

    private <ReqT, RespT> ServerCall.Listener<ReqT> close(final ServerCall<ReqT, RespT> serverCall,
                                                          final Metadata metadata,
                                                          final Status status) {
        serverCall.close(status, metadata);
        return new ServerCall.Listener<ReqT>() {
        };
    }
}
