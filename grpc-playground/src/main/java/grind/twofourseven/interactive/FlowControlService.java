package grind.twofourseven.interactive;

import grind.twofourseven.model.interactive.FlowControlServiceGrpc;
import grind.twofourseven.model.interactive.Output;
import grind.twofourseven.model.interactive.RequestSize;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.IntStream;

public class FlowControlService extends FlowControlServiceGrpc.FlowControlServiceImplBase {
    private static final Logger LOGGER = LoggerFactory.getLogger(FlowControlService.class);

    @Override
    public StreamObserver<RequestSize> getMessages(final StreamObserver<Output> responseObserver) {
        return new RequestHandler(responseObserver);
    }

    private static class RequestHandler implements StreamObserver<RequestSize> {
        private final StreamObserver<Output> responseObserver;
        private Integer emitted;

        public RequestHandler(final StreamObserver<Output> responseObserver) {
            this.responseObserver = responseObserver;
            emitted = 0;
        }

        @Override
        public void onNext(final RequestSize requestSize) {
            IntStream.rangeClosed((emitted + 1), 100)
                    .limit(requestSize.getSize())
                    .forEach(i -> {
                        LOGGER.info("emitting: {}", i);
                        responseObserver.onNext(Output.newBuilder().setValue(i).build());
                    });
            emitted += requestSize.getSize();
            if (emitted >= 100) responseObserver.onCompleted();
        }

        @Override
        public void onError(final Throwable throwable) {
            // we're not doing anything at this point.
        }

        @Override
        public void onCompleted() {
            LOGGER.info("User Ended the Streaming.");
            this.responseObserver.onCompleted();
        }
    }
}
