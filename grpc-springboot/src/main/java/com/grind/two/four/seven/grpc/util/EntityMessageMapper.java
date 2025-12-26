package com.grind.two.four.seven.grpc.util;

import com.grind.two.four.seven.grpc.entity.PortfolioItem;
import com.grind.two.four.seven.grpc.entity.User;
import com.grind.two.four.seven.grpc.user.Holding;
import com.grind.two.four.seven.grpc.user.StockTradeRequest;
import com.grind.two.four.seven.grpc.user.StockTradeResponse;
import com.grind.two.four.seven.grpc.user.UserInformation;

import java.util.List;

public class EntityMessageMapper {

    private EntityMessageMapper() {
    }

    public static UserInformation toUserInformation(final User user, List<PortfolioItem> portfolioItems) {
        var holdings = portfolioItems.stream()
                .map(portfolioItem -> Holding.newBuilder()
                        .setTicker(portfolioItem.getTicker()).setQuantity(portfolioItem.getQuantity())
                        .build()).toList();
        return UserInformation.newBuilder()
                .setUserId(user.getId())
                .setName(user.getName())
                .setBalance(user.getBalance())
                .addAllHoldings(holdings)
                .build();
    }

    public static PortfolioItem toPortfolioItem(final StockTradeRequest stockTradeRequest) {
        var portfolioItem = new PortfolioItem();
        portfolioItem.setUserId(stockTradeRequest.getUserId());
        portfolioItem.setTicker(stockTradeRequest.getTicker());
        portfolioItem.setQuantity(stockTradeRequest.getQuantity());
        return portfolioItem;
    }

    public static StockTradeResponse toStockTradeResponse(final StockTradeRequest stockTradeRequest, int balance) {
        return StockTradeResponse.newBuilder()
                .setUserId(stockTradeRequest.getUserId())
                .setPrice(stockTradeRequest.getPrice())
                .setTicker(stockTradeRequest.getTicker())
                .setQuantity(stockTradeRequest.getQuantity())
                .setAction(stockTradeRequest.getAction())
                .setTotalPrice(stockTradeRequest.getPrice() * stockTradeRequest.getQuantity())
                .setBalance(balance)
                .build();
    }
}
