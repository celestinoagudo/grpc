package com.grind.two.four.seven.grpc.exceptions;

public class InsufficientSharesException extends RuntimeException {

    private static final String MESSAGE = "User [id=%s] does not have enough shares to complete the transaction";

    public InsufficientSharesException(final Integer userId) {
        super(MESSAGE.formatted(userId));
    }
}
