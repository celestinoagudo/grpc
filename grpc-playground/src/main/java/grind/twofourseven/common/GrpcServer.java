package grind.twofourseven.common;

import io.grpc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class GrpcServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(GrpcServer.class);

    private final Server server;

    public GrpcServer(final Server server) {
        this.server = server;
    }

    public static GrpcServer create(final BindableService... services) {
        return create(6565, services);
    }

    public static GrpcServer create(final int port, final BindableService... services) {
        var builder = ServerBuilder.forPort(port);
        Arrays.asList(services).forEach(builder::addService);
        return new GrpcServer(builder.build());
    }

    public GrpcServer start() {
        var services = server.getServices().stream()
                .map(ServerServiceDefinition::getServiceDescriptor)
                .map(ServiceDescriptor::getName)
                .toList();
        try {
            server.start();
            LOGGER.info("Successfully started server on port: {}, with services: {}", server.getPort(), services);
            return this;
        } catch (Exception e) {
            throw new RuntimeException("Exception encountered while running server!", e);
        }
    }

    public void await() {
        try {
            server.awaitTermination();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void stop() {
        server.shutdownNow();
        LOGGER.info("Server stopped");
    }
}
