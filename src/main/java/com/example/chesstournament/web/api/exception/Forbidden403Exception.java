package com.example.chesstournament.web.api.exception;

public class Forbidden403Exception extends RuntimeException {

    public Forbidden403Exception(String message) {
        super(message);
    }
}
