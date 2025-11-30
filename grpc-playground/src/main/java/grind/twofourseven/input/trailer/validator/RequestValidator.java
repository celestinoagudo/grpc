package grind.twofourseven.input.trailer.validator;

import grind.twofourseven.model.input.trailer.ErrorMessage;
import grind.twofourseven.model.input.trailer.ValidationCode;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.protobuf.ProtoUtils;

import java.util.Optional;

public class RequestValidator {

    private static final Metadata.Key<ErrorMessage> ERROR_MESSAGE_KEY =
            ProtoUtils.keyForProto(ErrorMessage.getDefaultInstance());

    private RequestValidator() {
    }

    public static Optional<StatusRuntimeException> validateAccount(int accountNumber) {
        if (accountNumber > 0 && accountNumber < 11) return Optional.empty();
        var metadata = toMetadata(ValidationCode.INVALID_ACCOUNT);
        return Optional.of(Status.INVALID_ARGUMENT.asRuntimeException(metadata));
    }

    public static Optional<StatusRuntimeException> isAmountDivisibleBy10(int amount) {
        if (amount > 0 && amount % 10 == 0) return Optional.empty();
        var metadata = toMetadata(ValidationCode.INVALID_AMOUNT);
        return Optional.of(Status.INVALID_ARGUMENT.asRuntimeException(metadata));
    }

    public static Optional<StatusRuntimeException> hasSufficientBalance(int amount, int balance) {
        if (amount <= balance) return Optional.empty();
        var metadata = toMetadata(ValidationCode.INVALID_AMOUNT);
        return Optional.of(Status.FAILED_PRECONDITION.asRuntimeException(metadata));
    }

    private static Metadata toMetadata(final ValidationCode validationCode) {
        var metadata = new Metadata();
        var errorMessage = ErrorMessage.newBuilder().setValidationCode(validationCode).build();
        metadata.put(ERROR_MESSAGE_KEY, errorMessage);
        return metadata;
    }
}
