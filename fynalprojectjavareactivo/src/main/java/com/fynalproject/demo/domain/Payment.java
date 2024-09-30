package com.fynalproject.demo.domain;

import java.math.BigDecimal;

public class Payment {
    private String userId;
    private BigDecimal amount;

    public Payment() {
    }

    public Payment(String userId, BigDecimal amount) {
        this.userId = userId;
        this.amount = amount;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
