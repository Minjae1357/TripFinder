package io.github.devup.tripfinder.cart.exception;

public class CartItemNotOwnerException extends RuntimeException {
    public CartItemNotOwnerException(String message) {
        super(message);
    }
}
