package io.github.devup.tripfinder.booking.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BookingItemResponse(
        Long bookingItemId,
        String roomName,
        String accommodationName,
        LocalDate checkInDate,
        LocalDate checkOutDate,
        Integer roomQuantity,
        Integer guestCount,
        BigDecimal unitPrice,
        BigDecimal subtotal
) {
}
