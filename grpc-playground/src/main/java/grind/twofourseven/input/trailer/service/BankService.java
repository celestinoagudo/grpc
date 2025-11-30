package grind.twofourseven.input.trailer.service;

import com.google.common.util.concurrent.Uninterruptibles;
import grind.twofourseven.input.trailer.repository.AccountRepository;
import grind.twofourseven.input.trailer.validator.RequestValidator;
import grind.twofourseven.model.input.trailer.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public class BankService extends BankServiceGrpc.BankServiceImplBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(BankService.class);

    /**
     * @param request          - provided by the client.
     * @param responseObserver - provided by the server.
     */
    @Override
    public void getAccountBalance(final BalanceCheckRequest request,
                                  final StreamObserver<AccountBalance> responseObserver) {
        RequestValidator.validateAccount(request.getAccountNumber())
                .ifPresentOrElse(responseObserver::onError,
                        () -> sendAccountBalance(request, responseObserver));
    }

    private void sendAccountBalance(final BalanceCheckRequest request,
                                    final StreamObserver<AccountBalance> responseObserver) {
        var accountNumber = request.getAccountNumber();
        var balance = AccountRepository.getBalance(accountNumber);
        var accountBalance = AccountBalance.newBuilder().setAccountNumber(accountNumber)
                .setBalance(balance)
                .build();
        responseObserver.onNext(accountBalance);
        responseObserver.onCompleted();
    }

    @Override
    public void withdraw(final WithdrawRequest request, final StreamObserver<Money> responseObserver) {
        RequestValidator.validateAccount(request.getAccountNumber())
                .or(() -> RequestValidator.isAmountDivisibleBy10(request.getAmount()))
                .or(() -> RequestValidator.hasSufficientBalance(request.getAmount(),
                        AccountRepository.getBalance(request.getAccountNumber())))
                .ifPresentOrElse(responseObserver::onError, () -> sendMoney(request, responseObserver));
    }

    private void sendMoney(final WithdrawRequest request, final StreamObserver<Money> responseObserver) {
        try {
            var accountNumber = request.getAccountNumber();
            var requestedAmount = request.getAmount();
            var accountBalance = AccountRepository.getBalance(accountNumber);

            if (requestedAmount > accountBalance) {
                responseObserver.onCompleted();
                return;
            }

            for (int i = 0; i < (requestedAmount / 10); ++i) {
                var money = Money.newBuilder().setAmount(10).build();
                responseObserver.onNext(money);
                LOGGER.info("Money Sent: {}", money);
                AccountRepository.deductAmount(accountNumber, 10);
                Uninterruptibles.sleepUninterruptibly(Duration.ofSeconds(1));
            }
            responseObserver.onCompleted();
        } catch (final Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }
}
