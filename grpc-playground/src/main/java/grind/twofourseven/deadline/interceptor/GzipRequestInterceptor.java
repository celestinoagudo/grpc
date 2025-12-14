package grind.twofourseven.deadline.interceptor;

import io.grpc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GzipRequestInterceptor implements ClientInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(GzipRequestInterceptor.class);

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(final MethodDescriptor<ReqT, RespT> methodDescriptor,
                                                               final CallOptions callOptions,
                                                               final Channel channel) {
        LOGGER.info("Configuring GZIP compression interceptor...");
        return channel.newCall(methodDescriptor, callOptions.withCompression("gzip"));
    }
}
