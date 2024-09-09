package com.example.reactividadtaller.domain;

import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class UpdateAccountRequest {

    private String accountId;
    private String newData;

    // Getters y Setters

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getNewData() {
        return newData;
    }

    public void setNewData(String newData) {
        this.newData = newData;
    }
}