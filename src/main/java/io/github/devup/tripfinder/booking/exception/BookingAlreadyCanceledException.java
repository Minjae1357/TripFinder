package io.github.devup.tripfinder.booking.exception;

public class BookingAlreadyCanceledException extends RuntimeException {
    public BookingAlreadyCanceledException(String message) {
        super(message);
    }
}
