package grind.twofourseven.patterns.request.handlers;

import grind.twofourseven.model.unary.AccountBalance;
import grind.twofourseven.model.unary.DepositRequest;
import grind.twofourseven.patterns.repository.AccountRepository;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DepositRequestHandler implements StreamObserver<DepositRequest> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DepositRequestHandler.class);
    private final StreamObserver<AccountBalance> responseObserver;
    private int accountNumber;

    public DepositRequestHandler(final StreamObserver<AccountBalance> responseObserver) {
        this.responseObserver = responseObserver;
    }

    @Override
    public void onNext(final DepositRequest depositRequest) {
        LOGGER.error("Deposit Request Acct. No. : {}", depositRequest.getAccountNumber());

        switch (depositRequest.getRequestCase()) {
            case ACCOUNT_NUMBER -> accountNumber = depositRequest.getAccountNumber();
            case MONEY -> AccountRepository.addAmount(accountNumber, depositRequest.getMoney().getAmount());
            default ->
                    throw new IllegalStateException("Unexpected value: %s".formatted(depositRequest.getRequestCase()));
        }
    }

    @Override
    public void onError(final Throwable throwable) {
        LOGGER.error("Client error: {}", throwable.getMessage());
    }

    @Override
    public void onCompleted() {
        var accountBalance = AccountBalance.newBuilder().setAccountNumber(accountNumber)
                .setBalance(AccountRepository.getBalance(accountNumber))
                .build();
        responseObserver.onNext(accountBalance);
        responseObserver.onCompleted();
    }
}
