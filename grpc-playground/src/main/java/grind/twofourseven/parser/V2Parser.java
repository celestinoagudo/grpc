package grind.twofourseven.parser;

import com.google.protobuf.InvalidProtocolBufferException;
import grind.twofourseven.model.versioning.v2.Television;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class V2Parser {
    private static final Logger LOGGER = LoggerFactory.getLogger(V2Parser.class);

    public static void main(String[] args) {

    }

    public static void parse(byte[] bytes) throws InvalidProtocolBufferException {
        var television = Television.parseFrom(bytes);
        LOGGER.info("Brand: {}", television.getBrand());
        LOGGER.info("Model: {}", television.getModel());
        LOGGER.info("Type: {}", television.getType());
    }
}
