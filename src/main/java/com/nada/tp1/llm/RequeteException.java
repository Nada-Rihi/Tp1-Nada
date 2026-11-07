package com.nada.tp1.llm;

public class RequeteException extends Exception {
    public RequeteException(String message) {
        super(message);
    }

    public RequeteException(String message, Throwable cause) {
        super(message, cause);
    }
}