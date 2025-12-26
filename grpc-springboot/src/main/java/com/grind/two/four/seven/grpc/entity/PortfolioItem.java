package com.grind.two.four.seven.grpc.entity;

import com.grind.two.four.seven.grpc.common.Ticker;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class PortfolioItem {

    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "customer_id")
    private Integer userId;
    private Ticker ticker;

    public Integer getId() {
        return id;
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(final Integer userId) {
        this.userId = userId;
    }

    public Ticker getTicker() {
        return ticker;
    }

    public void setTicker(final Ticker ticker) {
        this.ticker = ticker;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(final Integer quantity) {
        this.quantity = quantity;
    }

    private Integer quantity;
}
