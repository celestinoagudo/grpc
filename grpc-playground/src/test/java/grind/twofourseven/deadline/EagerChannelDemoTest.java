package grind.twofourseven.deadline;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class EagerChannelDemoTest extends AbstractChannelTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(EagerChannelDemoTest.class);

    @Test
    void eagerChannelDemo() {
        LOGGER.info("{}", managedChannel.getState(true)); //make the connection request eager.
    }
}
