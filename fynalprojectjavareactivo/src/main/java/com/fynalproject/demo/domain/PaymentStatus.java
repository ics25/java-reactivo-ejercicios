package com.fynalproject.demo.domain;

public enum PaymentStatus {
    INVALID("INVALID"),
    APPROVED("APPROVED"),
    REJECTED("REJECTED");

    private String status;

    PaymentStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
