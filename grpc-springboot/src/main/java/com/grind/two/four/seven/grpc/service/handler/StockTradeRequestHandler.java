package com.grind.two.four.seven.grpc.service.handler;

import com.grind.two.four.seven.grpc.common.Ticker;
import com.grind.two.four.seven.grpc.exceptions.InsufficientBalanceException;
import com.grind.two.four.seven.grpc.exceptions.UnknownTickerException;
import com.grind.two.four.seven.grpc.exceptions.UnknownUserException;
import com.grind.two.four.seven.grpc.repository.PortfolioItemRepository;
import com.grind.two.four.seven.grpc.repository.UserRepository;
import com.grind.two.four.seven.grpc.user.StockTradeRequest;
import com.grind.two.four.seven.grpc.user.StockTradeResponse;
import com.grind.two.four.seven.grpc.util.EntityMessageMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class StockTradeRequestHandler {

    private final UserRepository userRepository;
    private final PortfolioItemRepository portfolioItemRepository;

    public StockTradeRequestHandler(final UserRepository userRepository, final PortfolioItemRepository portfolioItemRepository) {
        this.userRepository = userRepository;
        this.portfolioItemRepository = portfolioItemRepository;
    }

    @Transactional
    public StockTradeResponse buyStock(final StockTradeRequest stockTradeRequest) {
        validateTicker(stockTradeRequest.getTicker());
        var user = userRepository.findById(stockTradeRequest.getUserId())
                .orElseThrow(() -> new UnknownUserException(stockTradeRequest.getUserId()));
        var totalPrice = stockTradeRequest.getQuantity() * stockTradeRequest.getPrice();
        validateUserBalance(stockTradeRequest.getUserId(), user.getBalance(), totalPrice);
        //valid request.
        user.setBalance(user.getBalance() - totalPrice);
        portfolioItemRepository.findByUserIdAndTicker(user.getId(), stockTradeRequest.getTicker())
                .ifPresentOrElse(
                        portfolioItem -> portfolioItem.setQuantity(portfolioItem.getQuantity()
                                + stockTradeRequest.getQuantity()),
                        () -> portfolioItemRepository.save(EntityMessageMapper.toPortfolioItem(stockTradeRequest))

                );
        return EntityMessageMapper.toStockTradeResponse(stockTradeRequest, user.getBalance());
    }

    private void validateTicker(final Ticker ticker) {
        if (Ticker.UNKNOWN.equals(ticker)) throw new UnknownTickerException();

    }

    private void validateUserBalance(final Integer userId, final Integer userBalance, final Integer totalPrice) {
        if (totalPrice > userBalance) throw new InsufficientBalanceException(userId);
    }


}
