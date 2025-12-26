package com.grind.two.four.seven.grpc.exceptions;

public class InsufficientBalanceException extends RuntimeException {

    private static final String MESSAGE = "User [id=%s] does not have enough fund to complete the transaction!";

    public InsufficientBalanceException(final Integer userId) {
        super(MESSAGE.formatted(userId));
    }
}
