package com.grind.two.four.seven.grpc.config;

import com.grind.two.four.seven.grpc.exceptions.InsufficientBalanceException;
import com.grind.two.four.seven.grpc.exceptions.InsufficientSharesException;
import com.grind.two.four.seven.grpc.exceptions.UnknownTickerException;
import com.grind.two.four.seven.grpc.exceptions.UnknownUserException;
import io.grpc.Status;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.server.exception.GrpcExceptionHandler;

@Configuration
public class ServiceExceptionHandler {

    @Bean
    public GrpcExceptionHandler handleInvalidArguments() {
        return (Throwable t) -> {
            if (t instanceof UnknownTickerException) {
                return Status.INVALID_ARGUMENT.withDescription(t.getMessage()).asException();
            }
            // Return null to let other handlers (or the default one) handle it
            return null;
        };
    }

    @Bean
    public GrpcExceptionHandler handleUnknownEntities() {
        return (Throwable t) -> {
            if (t instanceof UnknownUserException) {
                return Status.NOT_FOUND.withDescription(t.getMessage()).asException();
            }
            // Return null to let other handlers (or the default one) handle it
            return null;
        };
    }

    @Bean
    public GrpcExceptionHandler handlePreconditionFailures() {
        return (Throwable t) -> {
            if (t instanceof InsufficientSharesException || t instanceof InsufficientBalanceException) {
                return Status.FAILED_PRECONDITION.withDescription(t.getMessage()).asException();
            }
            // Return null to let other handlers (or the default one) handle it
            return null;
        };
    }
}
