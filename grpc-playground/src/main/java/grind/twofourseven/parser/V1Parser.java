package grind.twofourseven.parser;

import com.google.protobuf.InvalidProtocolBufferException;
import grind.twofourseven.model.versioning.v1.Television;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class V1Parser {
    private static final Logger LOGGER = LoggerFactory.getLogger(V1Parser.class);

    public static void parse(byte[] bytes) throws InvalidProtocolBufferException {
        var television = Television.parseFrom(bytes);
        LOGGER.info("Brand: {}", television.getBrand());
        LOGGER.info("Year: {}", television.getYear());
        LOGGER.info("Unknown Fields: {}", television.getUnknownFields());
    }
}
