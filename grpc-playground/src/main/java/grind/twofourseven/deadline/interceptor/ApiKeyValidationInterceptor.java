package grind.twofourseven.deadline.interceptor;

import io.grpc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class ApiKeyValidationInterceptor implements ServerInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiKeyValidationInterceptor.class);
    private static final Metadata.Key<String> API_KEY = Metadata.Key.of("api-key", Metadata.ASCII_STRING_MARSHALLER);

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(final ServerCall<ReqT, RespT> serverCall,
                                                                 final Metadata metadata,
                                                                 final ServerCallHandler<ReqT, RespT> serverCallHandler) {
        LOGGER.info("{}", serverCall.getMethodDescriptor().getFullMethodName());
        var apiKey = metadata.get(API_KEY);
        if (isValid(apiKey)) {
            return serverCallHandler.startCall(serverCall, metadata);
        }
        serverCall.close(Status.UNAUTHENTICATED.withDescription("Client must provide valid api key"), metadata);

        return new ServerCall.Listener<ReqT>() {
        };
    }

    private boolean isValid(final String apiKey) {
        return Objects.nonNull(apiKey) && apiKey.equals("bank-client-secret");
    }
}
