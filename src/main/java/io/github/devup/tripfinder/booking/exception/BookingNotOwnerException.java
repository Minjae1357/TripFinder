package io.github.devup.tripfinder.booking.exception;

public class BookingNotOwnerException extends RuntimeException {
    public BookingNotOwnerException(String message) {
        super(message);
    }
}
