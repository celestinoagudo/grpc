package grind.twofourseven.deadline;

import com.google.common.util.concurrent.Uninterruptibles;
import grind.twofourseven.model.deadline.AccountBalance;
import grind.twofourseven.model.deadline.BalanceCheckRequest;
import grind.twofourseven.model.deadline.BankServiceGrpc;
import io.grpc.Context;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public class UserRoleDeadlineBankService extends BankServiceGrpc.BankServiceImplBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserRoleDeadlineBankService.class);
    private static final Context.Key<UserRole> USER_ROLE_KEY = Context.key("user-role");

    /**
     * @param request          - provided by the client.
     * @param responseObserver - provided by the server.
     */
    @Override
    public void getAccountBalance(final BalanceCheckRequest request,
                                  final StreamObserver<AccountBalance> responseObserver) {
        var accountNumber = request.getAccountNumber();
        var balance = AccountRepository.getBalance(accountNumber);
        if (UserRole.STANDARD.equals(USER_ROLE_KEY.get())) {
            var fee = balance > 0 ? 1 : 0;
            AccountRepository.deductAmount(accountNumber, fee);
            balance -= fee;
            LOGGER.info("Updated Balance: {}", balance);
        }

        var accountBalance = AccountBalance.newBuilder().setAccountNumber(accountNumber)
                .setBalance(balance)
                .build();
        USER_ROLE_KEY.get();
        Uninterruptibles.sleepUninterruptibly(Duration.ofSeconds(3));
        responseObserver.onNext(accountBalance);
        responseObserver.onCompleted();
    }
}
