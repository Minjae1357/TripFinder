package io.github.devup.tripfinder.booking.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record BookingResponse(
        Long bookingId,
        String bookingStatus,
        BigDecimal totalAmount,
        LocalDateTime createdAt,
        List<BookingItemResponse> items
) {
}
