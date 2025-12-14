package grind.twofourseven.deadline.interceptor;

import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;

public class GzipResponseInterceptor implements ServerInterceptor {
    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(final ServerCall<ReqT, RespT> serverCall,
                                                                 final Metadata metadata, final ServerCallHandler<ReqT, RespT> serverCallHandler) {
        serverCall.setCompression("gzip");
        return serverCallHandler.startCall(serverCall, metadata);
    }
}
