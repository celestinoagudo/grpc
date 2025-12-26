package com.grind.two.four.seven.grpc.exceptions;

public class UnknownUserException extends RuntimeException {

    private static final String MESSAGE = "User [id=%d] is not found!";

    public UnknownUserException(final Integer userId) {
        super(MESSAGE.formatted(userId));
    }
}
