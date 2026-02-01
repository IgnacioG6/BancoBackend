package com.example.banco.exception;

public class EstadoInvalidoException extends RuntimeException {
    public EstadoInvalidoException(String message) {
        super(message);
    }
}
