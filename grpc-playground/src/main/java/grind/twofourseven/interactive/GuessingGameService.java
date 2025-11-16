package grind.twofourseven.interactive;

import grind.twofourseven.model.interactive.GuessNumberGrpc;
import grind.twofourseven.model.interactive.GuessRequest;
import grind.twofourseven.model.interactive.GuessResponse;
import grind.twofourseven.model.interactive.Result;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadLocalRandom;

public class GuessingGameService extends GuessNumberGrpc.GuessNumberImplBase {
    private static final Logger LOGGER = LoggerFactory.getLogger(GuessingGameService.class);

    @Override
    public StreamObserver<GuessRequest> makeGuess(final StreamObserver<GuessResponse> responseObserver) {
        return new RequestHandler(responseObserver);
    }

    private static class RequestHandler implements StreamObserver<GuessRequest> {
        private final StreamObserver<GuessResponse> guessResponseStreamObserver;
        private final int number;
        private int attempt;

        public RequestHandler(final StreamObserver<GuessResponse> guessResponseStreamObserver) {
            this.guessResponseStreamObserver = guessResponseStreamObserver;
            this.number = ThreadLocalRandom.current().nextInt(1, 11);
            LOGGER.info("Number to Guess: {}", number);
        }

        @Override
        public void onNext(final GuessRequest guessRequest) {
            ++attempt;
            var guessResponseBuilder = GuessResponse.newBuilder();
            var guessedNumber = guessRequest.getGuess();
            if (guessedNumber > number) {
                guessResponseBuilder.setAttempt(attempt).setResult(Result.TOO_HIGH);
                guessResponseStreamObserver.onNext(guessResponseBuilder.build());
            } else if (guessedNumber < number) {
                guessResponseBuilder.setAttempt(attempt).setResult(Result.TOO_LOW);
                guessResponseStreamObserver.onNext(guessResponseBuilder.build());
            } else {
                LOGGER.info("Congratulations you got it right!");
                guessResponseBuilder.setAttempt(attempt).setResult(Result.CORRECT);
                guessResponseStreamObserver.onNext(guessResponseBuilder.build());
                guessResponseStreamObserver.onCompleted();
                attempt = 0;
            }
        }

        @Override
        public void onError(final Throwable throwable) {
            //we're not doing anything at this point.
        }

        @Override
        public void onCompleted() {
            LOGGER.info("User Ended the Streaming.");
            this.guessResponseStreamObserver.onCompleted();
        }
    }
}
