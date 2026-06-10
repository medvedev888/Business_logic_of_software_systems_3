package me.ifmo.backend.exceptions;

public class KafkaEventPublishingException extends RuntimeException {
    public KafkaEventPublishingException(String message, Throwable cause) {
        super(message, cause);
    }
}