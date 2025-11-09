package grind.twofourseven.patterns.service;

import com.google.common.util.concurrent.Uninterruptibles;
import com.google.protobuf.Empty;
import grind.twofourseven.model.unary.*;
import grind.twofourseven.patterns.repository.AccountRepository;
import grind.twofourseven.patterns.request.handlers.DepositRequestHandler;
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
        var accountNumber = request.getAccountNumber();
        var balance = AccountRepository.getBalance(accountNumber);
        var accountBalance = AccountBalance.newBuilder().setAccountNumber(accountNumber)
                .setBalance(balance)
                .build();
        responseObserver.onNext(accountBalance);
        responseObserver.onCompleted();
    }

    @Override
    public void getAllAccounts(final Empty request, final StreamObserver<AllAccountsResponse> responseObserver) {
        var accounts = AccountRepository.getAllAccounts()
                .entrySet().stream().map(entry ->
                        AccountBalance.newBuilder()
                                .setAccountNumber(entry.getKey()).setBalance(entry.getValue()).build())
                .toList();
        var response = AllAccountsResponse.newBuilder().addAllAccounts(accounts).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void withdraw(final WithdrawRequest request, final StreamObserver<Money> responseObserver) {
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
    }

    /**
     * @param responseObserver - sent to server (outgoing).
     * @return StreamObserver - returns by the Server (incoming).
     */
    @Override
    public StreamObserver<DepositRequest> deposit(final StreamObserver<AccountBalance> responseObserver) {
        return new DepositRequestHandler(responseObserver);
    }
}
