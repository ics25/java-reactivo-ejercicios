package com.fynalproject.demo.domain;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;


public class Balance {
    @NotNull(message = "Amount is required")
    private BigDecimal amount;
    public Balance() {
    }
    public Balance(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
