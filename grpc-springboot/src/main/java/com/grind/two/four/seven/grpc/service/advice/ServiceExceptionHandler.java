package com.grind.two.four.seven.grpc.service.advice;

import com.grind.two.four.seven.grpc.exceptions.InsufficientBalanceException;
import com.grind.two.four.seven.grpc.exceptions.InsufficientSharesException;
import com.grind.two.four.seven.grpc.exceptions.UnknownTickerException;
import com.grind.two.four.seven.grpc.exceptions.UnknownUserException;
import io.grpc.Status;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;

@GrpcAdvice
public class ServiceExceptionHandler {

    @GrpcExceptionHandler(UnknownTickerException.class)
    public Status handleInvalidArguments(final UnknownTickerException e) {
        return Status.INVALID_ARGUMENT.withDescription(e.getMessage());
    }

    @GrpcExceptionHandler(UnknownUserException.class)
    public Status handleUnknownEntities(final UnknownUserException e) {
        return Status.NOT_FOUND.withDescription(e.getMessage());
    }

    @GrpcExceptionHandler({InsufficientSharesException.class, InsufficientBalanceException.class})
    public Status handlePreconditionFailures(final Exception e) {
        return Status.NOT_FOUND.withDescription(e.getMessage());
    }
}
