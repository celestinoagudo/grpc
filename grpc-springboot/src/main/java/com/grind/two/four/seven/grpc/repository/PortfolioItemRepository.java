package com.grind.two.four.seven.grpc.repository;

import com.grind.two.four.seven.grpc.common.Ticker;
import com.grind.two.four.seven.grpc.entity.PortfolioItem;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PortfolioItemRepository extends CrudRepository<PortfolioItem, Integer> {

    List<PortfolioItem> findAllByUserId(final Integer userId);

    Optional<PortfolioItem> findByUserIdAndTicker(final Integer userId, final Ticker ticker);
}
