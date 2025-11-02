package grind.twofourseven.unary.service;

import com.google.protobuf.Empty;
import grind.twofourseven.model.unary.AccountBalance;
import grind.twofourseven.model.unary.AllAccountsResponse;
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
}
