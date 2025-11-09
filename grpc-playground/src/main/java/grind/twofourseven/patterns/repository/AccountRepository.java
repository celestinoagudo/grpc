package grind.twofourseven.patterns.repository;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AccountRepository {

    private AccountRepository() {
    }

    private static final Map<Integer, Integer> db = IntStream.rangeClosed(1, 10)
            .boxed().collect(Collectors.toMap(Function.identity(), v -> 100));

    public static Integer getBalance(final int accountNumber) {
        return db.get(accountNumber);
    }

    public static Map<Integer, Integer> getAllAccounts() {
        return Collections.unmodifiableMap(db);
    }

    public static void deductAmount(int accountNumber, int amount) {
        db.computeIfPresent(accountNumber, (_, balance) -> balance - amount);
    }

    public static void addAmount(int accountNumber, int amount) {
        db.computeIfPresent(accountNumber, (_, balance) -> balance + amount);
    }
}
