package grind.twofourseven.unary.service;

import grind.twofourseven.model.unary.AccountBalance;
import grind.twofourseven.model.unary.BalanceCheckRequest;
import grind.twofourseven.model.unary.BankServiceGrpc;
import grind.twofourseven.unary.repository.AccountRepository;
import io.grpc.stub.StreamObserver;

public class BankService extends BankServiceGrpc.BankServiceImplBase {

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
}
