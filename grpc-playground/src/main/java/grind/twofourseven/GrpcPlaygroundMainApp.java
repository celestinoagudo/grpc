package grind.twofourseven;

import grind.twofourseven.model.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GrpcPlaygroundMainApp {

    private static final Logger LOGGER = LoggerFactory.getLogger(GrpcPlaygroundMainApp.class);

    public static void main(String[] args) {

        var person = Person.newBuilder().setName("Winter Solder").setAge(32).build();
        LOGGER.info("{}", person);
    }
}