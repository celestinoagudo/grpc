package grind.twofourseven.deadline.interceptor;

import io.grpc.*;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class DeadlineInterceptor implements ClientInterceptor {

    private final Duration duration;

    public DeadlineInterceptor(final Duration duration) {
        this.duration = duration;
    }

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(final MethodDescriptor<ReqT, RespT> methodDescriptor,
                                                               CallOptions callOptions, final Channel channel) {
        callOptions = Objects.nonNull(callOptions.getDeadline()) ? callOptions :
                callOptions.withDeadline(Deadline.after(duration.toMillis(), TimeUnit.MILLISECONDS)); //allows overriding of timeout
        return channel.newCall(methodDescriptor, callOptions);
    }
}
