package grind.twofourseven.common;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;

import java.util.concurrent.TimeUnit;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class AbstractChannelTest {

    protected ManagedChannel channel;

    @BeforeEach
    public void setupChannel() {
        this.channel = ManagedChannelBuilder.forAddress("localhost", 6565)
                .usePlaintext().build();
    }

    @AfterEach
    public void stopChannel() throws InterruptedException {
        channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
    }

}