package com.example.chesstournament.web.api.exception;

public class Unauthorized401Exception extends RuntimeException {
    public Unauthorized401Exception(String message) {
        super(message);
    }
}
