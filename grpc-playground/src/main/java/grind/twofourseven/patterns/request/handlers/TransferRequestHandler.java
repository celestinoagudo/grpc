package grind.twofourseven.patterns.request.handlers;

import grind.twofourseven.model.unary.AccountBalance;
import grind.twofourseven.model.unary.TransferRequest;
import grind.twofourseven.model.unary.TransferResponse;
import grind.twofourseven.model.unary.TransferStatus;
import grind.twofourseven.patterns.repository.AccountRepository;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransferRequestHandler implements StreamObserver<TransferRequest> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransferRequestHandler.class);
    private final StreamObserver<TransferResponse> responseObserver;

    public TransferRequestHandler(final StreamObserver<TransferResponse> responseObserver) {
        this.responseObserver = responseObserver;
    }

    @Override
    public void onNext(final TransferRequest transferRequest) {
        var status = transfer(transferRequest);
        var response = TransferResponse.newBuilder()
                .setFromAccount(toAccountBalance(transferRequest.getFromAccount()))
                .setToAccount(toAccountBalance(transferRequest.getToAccount()))
                .setStatus(status)
                .build();
        responseObserver.onNext(response);

    }

    @Override
    public void onError(final Throwable throwable) {
        LOGGER.error("Client Error: {}", throwable.getMessage());
    }

    @Override
    public void onCompleted() {
        LOGGER.info("Transfer Completed!");
        responseObserver.onCompleted();
    }

    private TransferStatus transfer(final TransferRequest transferRequest) {
        final var amount = transferRequest.getAmount();
        final var fromAccount = transferRequest.getFromAccount();
        final var toAccount = transferRequest.getToAccount();
        var status = TransferStatus.REJECTED;
        if (AccountRepository.getBalance(fromAccount) >= amount && (fromAccount != toAccount)) {
            AccountRepository.deductAmount(fromAccount, amount);
            AccountRepository.addAmount(toAccount, amount);
            status = TransferStatus.COMPLETED;
        }
        return status;
    }

    private AccountBalance toAccountBalance(final int accountNumber) {
        return AccountBalance.newBuilder().setAccountNumber(accountNumber)
                .setBalance(AccountRepository.getBalance(accountNumber)).build();
    }
}
