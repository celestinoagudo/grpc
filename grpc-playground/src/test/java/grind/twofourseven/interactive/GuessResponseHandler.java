package grind.twofourseven.interactive;

import com.google.common.util.concurrent.Uninterruptibles;
import grind.twofourseven.model.interactive.GuessRequest;
import grind.twofourseven.model.interactive.GuessResponse;
import grind.twofourseven.model.interactive.Result;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class GuessResponseHandler implements StreamObserver<GuessResponse> {
    private static final Logger LOGGER = LoggerFactory.getLogger(GuessResponseHandler.class);
    private final CountDownLatch latch = new CountDownLatch(1);
    private StreamObserver<GuessRequest> guessRequestObserver;
    private int guessedNumber;

    @Override
    public void onNext(final GuessResponse guessResponse) {
        LOGGER.info("Received: {}", guessResponse);
        Uninterruptibles.sleepUninterruptibly(ThreadLocalRandom.current().nextInt(50, 200),
                TimeUnit.MILLISECONDS);
        if (Result.TOO_HIGH.equals(guessResponse.getResult())) {
            guess(ThreadLocalRandom.current().nextInt(1, guessedNumber));
        } else if (Result.TOO_LOW.equals(guessResponse.getResult())) {
            guess(ThreadLocalRandom.current().nextInt(guessedNumber, 11));
        } else {
            guessRequestObserver.onCompleted();
            LOGGER.info("Successfully Guessed!");
            latch.countDown();
        }
    }

    public void setGuessRequestObserver(final StreamObserver<GuessRequest> guessRequestObserver) {
        this.guessRequestObserver = guessRequestObserver;
    }

    @Override
    public void onError(final Throwable throwable) {
        latch.countDown();
    }

    @Override
    public void onCompleted() {
        LOGGER.info("Completed!");
        latch.countDown();
    }

    private void guess(final int guessedNumber) {
        LOGGER.info("Guess: {}", guessedNumber);
        this.guessedNumber = guessedNumber;
        guessRequestObserver.onNext(GuessRequest.newBuilder().setGuess(guessedNumber).build());
    }

    public void start() {
        guess(ThreadLocalRandom.current().nextInt(1, 11));
    }

    public void await() {
        try {
            latch.await();
        } catch (final InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
