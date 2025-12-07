package grind.twofourseven.deadline;

import com.google.common.util.concurrent.Uninterruptibles;
import grind.twofourseven.model.deadline.*;
import io.grpc.Context;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public class DeadlineBankService extends BankServiceGrpc.BankServiceImplBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeadlineBankService.class);

    /**
     * @param request          - provided by the client.
     * @param responseObserver - provided by the server.
     */
    @Override
    public void getAccountBalance(final BalanceCheckRequest request,
                                  final StreamObserver<AccountBalance> responseObserver) {
        var accountNumber = request.getAccountNumber();
        var balance = AccountRepository.getBalance(accountNumber);
        var accountBalance = AccountBalance.newBuilder().setAccountNumber(accountNumber)
                .setBalance(balance)
                .build();
        Uninterruptibles.sleepUninterruptibly(Duration.ofSeconds(3));
        responseObserver.onNext(accountBalance);
        responseObserver.onCompleted();
    }

    @Override
    public void withdraw(final WithdrawRequest request, final StreamObserver<Money> responseObserver) {
        var accountNumber = request.getAccountNumber();
        var requestedAmount = request.getAmount();
        var accountBalance = AccountRepository.getBalance(accountNumber);

        if (requestedAmount > accountBalance) {
            responseObserver.onError(Status.FAILED_PRECONDITION.asRuntimeException());
            return;
        }

        for (int i = 0; i < (requestedAmount / 10) && !Context.current().isCancelled(); ++i) {
            var money = Money.newBuilder().setAmount(10).build();
            responseObserver.onNext(money);
            LOGGER.info("Money Sent: {}", money);
            AccountRepository.deductAmount(accountNumber, 10);
            Uninterruptibles.sleepUninterruptibly(Duration.ofSeconds(1));
        }
        responseObserver.onCompleted();
    }
}
