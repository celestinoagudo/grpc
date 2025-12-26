package com.grind.two.four.seven.grpc.util;

import com.grind.two.four.seven.grpc.entity.PortfolioItem;
import com.grind.two.four.seven.grpc.entity.User;
import com.grind.two.four.seven.grpc.user.Holding;
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
}
