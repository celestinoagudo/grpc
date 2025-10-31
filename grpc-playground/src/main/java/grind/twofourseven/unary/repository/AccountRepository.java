package grind.twofourseven.unary.repository;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AccountRepository {
    private static final Map<Integer, Integer> db = IntStream.range(1, 10)
            .boxed().collect(Collectors.toMap(Function.identity(), v -> 100));

    public static Integer getBalance(final int accountNumber) {
        return db.get(accountNumber);
    }
}
