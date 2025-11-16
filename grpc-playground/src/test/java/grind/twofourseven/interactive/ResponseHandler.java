package grind.twofourseven.interactive;

import com.google.common.util.concurrent.Uninterruptibles;
import grind.twofourseven.model.interactive.Output;
import grind.twofourseven.model.interactive.RequestSize;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class ResponseHandler implements StreamObserver<Output> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResponseHandler.class);
    private final CountDownLatch latch = new CountDownLatch(1);
    private StreamObserver<RequestSize> requestSizeStreamObserver;
    private int size;

    @Override
    public void onNext(final Output output) {
        this.size--;
        process(output);
        if (size == 0) {
            LOGGER.info("-----------");
            request(ThreadLocalRandom.current().nextInt(1, 6));
        }
    }

    @Override
    public void onError(final Throwable throwable) {
        latch.countDown();
    }

    @Override
    public void onCompleted() {
        requestSizeStreamObserver.onCompleted();
        LOGGER.info("Completed!");
        latch.countDown();
    }

    public void setRequestSizeStreamObserver(final StreamObserver<RequestSize> requestSizeStreamObserver) {
        this.requestSizeStreamObserver = requestSizeStreamObserver;
    }

    private void request(final int size) {
        LOGGER.info("Requesting Size: {}", size);
        this.size = size;
        requestSizeStreamObserver.onNext(RequestSize.newBuilder().setSize(size).build());
    }

    private void process(final Output output) {
        LOGGER.info("Received: {}", output);
        Uninterruptibles.sleepUninterruptibly(ThreadLocalRandom.current().nextInt(50, 200),
                TimeUnit.MILLISECONDS);
    }

    public void await() {
        try {
            latch.await();
        } catch (final InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void start() {
        request(3);
    }
}
