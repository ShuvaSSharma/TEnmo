package com.techelevator.tenmo.exception;


public class BalanceInsufficientException extends RuntimeException{
    public BalanceInsufficientException(String message){
        super(message);
    }
}
