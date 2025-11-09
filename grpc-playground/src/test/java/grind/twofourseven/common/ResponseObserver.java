package grind.twofourseven.common;

import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ResponseObserver<T> implements StreamObserver<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResponseObserver.class);
    private final List<T> items = Collections.synchronizedList(new ArrayList<>());
    private final CountDownLatch countDownLatch;
    private Throwable throwable;

    private ResponseObserver(final int countDown) {
        countDownLatch = new CountDownLatch(countDown);
    }

    public static <T> ResponseObserver<T> create() {
        return new ResponseObserver<>(1);
    }

    public static <T> ResponseObserver<T> create(final int countDown) {
        return new ResponseObserver<>(countDown);
    }

    @Override
    public void onNext(final T t) {
        LOGGER.info("Received item: {}", t);
        items.add(t);
    }

    @Override
    public void onError(final Throwable throwable) {
        LOGGER.info("Received error: {}", throwable.getMessage());
        this.throwable = throwable;
        countDownLatch.countDown();
    }

    @Override
    public void onCompleted() {
        LOGGER.info("Completed!");
        countDownLatch.countDown();
    }

    public void await() {
        try {
            countDownLatch.await(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<T> getItems() {
        return Collections.unmodifiableList(items);
    }

    public Throwable getThrowable() {
        return throwable;
    }
}
