package grind.twofourseven.interactive;

import grind.twofourseven.common.AbstractChannelTest;
import grind.twofourseven.common.GrpcServer;
import grind.twofourseven.model.interactive.GuessNumberGrpc;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GuessingGameServiceTest extends AbstractChannelTest {

    private final GrpcServer server = GrpcServer.create(new GuessingGameService());
    private GuessNumberGrpc.GuessNumberStub guessNumberAsyncStub;

    @BeforeAll
    void setup() {
        server.start();
        guessNumberAsyncStub = GuessNumberGrpc.newStub(channel);
    }

    @Test
    void guessingGameTest() {
        var responseObserver = new GuessResponseHandler();
        var requestObserver = guessNumberAsyncStub.makeGuess(responseObserver);
        responseObserver.setGuessRequestObserver(requestObserver);
        responseObserver.start();
        responseObserver.await();
    }

    @AfterAll
    void stop() {
        server.stop();
    }
}