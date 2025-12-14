package grind.twofourseven.deadline.interceptor;

import io.grpc.*;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class DeadlineInterceptor implements ClientInterceptor {

    private final Duration duration;

    public DeadlineInterceptor(final Duration duration) {
        this.duration = duration;
    }

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(final MethodDescriptor<ReqT, RespT> methodDescriptor,
                                                               CallOptions callOptions, final Channel channel) {
        callOptions = callOptions.withDeadline(Deadline.after(duration.toMillis(), TimeUnit.MILLISECONDS));
        return channel.newCall(methodDescriptor, callOptions);
    }
}
