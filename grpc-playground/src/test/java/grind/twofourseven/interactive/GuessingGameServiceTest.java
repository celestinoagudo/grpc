package grind.twofourseven.interactive;

import grind.twofourseven.common.AbstractChannelTest;
import grind.twofourseven.common.GrpcServer;
import grind.twofourseven.model.interactive.GuessNumberGrpc;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GuessingGameServiceTest extends AbstractChannelTest {

    private final GrpcServer server = GrpcServer.create(new GuessingGameService());
    private GuessNumberGrpc.GuessNumberStub guessNumberAsyncStub;
}