package com.grind.two.four.seven.grpc.service;

import com.grind.two.four.seven.grpc.common.Ticker;
import com.grind.two.four.seven.grpc.user.StockTradeRequest;
import com.grind.two.four.seven.grpc.user.TradeAction;
import com.grind.two.four.seven.grpc.user.UserInformationRequest;
import com.grind.two.four.seven.grpc.user.UserServiceGrpc;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {"spring.grpc.client.default-channel.address=localhost:6565"})
class UserServiceTest {

    @Autowired
    private UserServiceGrpc.UserServiceBlockingStub userService;

    @Test
    void shouldRetrieveUserInformation() {
        final var request = UserInformationRequest.newBuilder().setUserId(1).build();
        final var response = userService.getUserInformation(request);
        assertAll(() -> assertNotNull(response),
                () -> assertEquals("Sam", response.getName()));
    }

    @Test
    void shouldReturnNotFoundWhenUserIsUnknown() {
        final var request = UserInformationRequest.newBuilder().setUserId(-1).build();
        final var e = assertThrows(StatusRuntimeException.class, () -> userService.getUserInformation(request));
        assertEquals(Status.NOT_FOUND.getCode(), e.getStatus().getCode());
    }

    @Test
    void shouldReturnInvalidArgumentWhenTickerIsUnknown() {
        final var request = StockTradeRequest.newBuilder().setUserId(1).setAction(TradeAction.BUY)
                .build();
        final var e = assertThrows(StatusRuntimeException.class, () -> userService.tradeStock(request));
        assertEquals(Status.INVALID_ARGUMENT.getCode(), e.getStatus().getCode());
    }

    @Test
    void shouldReturnFailedPreconditionWhenInsufficientShares() {
        final var request = StockTradeRequest.newBuilder().setUserId(1).setTicker(Ticker.AMAZON).setAction(TradeAction.SELL)
                .setQuantity(1000).setPrice(1000)
                .build();
        final var e = assertThrows(StatusRuntimeException.class, () -> userService.tradeStock(request));
        assertEquals(Status.FAILED_PRECONDITION.getCode(), e.getStatus().getCode());
    }

    @Test
    void shouldReturnFailedPreconditionWhenInsufficientBalance() {
        final var request = StockTradeRequest.newBuilder().setUserId(1).setTicker(Ticker.AMAZON).setAction(TradeAction.BUY)
                .setQuantity(1000).setPrice(100000)
                .build();
        final var e = assertThrows(StatusRuntimeException.class, () -> userService.tradeStock(request));
        assertEquals(Status.FAILED_PRECONDITION.getCode(), e.getStatus().getCode());
    }

    @Test
    void shouldNotThrowExceptionWhenAllIsWellWhileSelling() {
        final var request = StockTradeRequest.newBuilder().setUserId(1).setTicker(Ticker.AMAZON).setAction(TradeAction.SELL)
                .setQuantity(1).setPrice(1)
                .build();
        assertDoesNotThrow(() -> userService.tradeStock(request));
    }

    @Test
    void shouldNotThrowExceptionWhenAllIsWellWhileBuying() {
        final var request = StockTradeRequest.newBuilder().setUserId(1).setTicker(Ticker.AMAZON).setAction(TradeAction.BUY)
                .setQuantity(1).setPrice(1)
                .build();
        assertDoesNotThrow(() -> userService.tradeStock(request));
    }
}