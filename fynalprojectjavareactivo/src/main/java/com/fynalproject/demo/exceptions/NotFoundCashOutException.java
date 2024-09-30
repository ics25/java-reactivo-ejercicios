package com.fynalproject.demo.exceptions;

public class NotFoundCashOutException extends RuntimeException{
    public NotFoundCashOutException(String message) {
        super(message);
    }
}
