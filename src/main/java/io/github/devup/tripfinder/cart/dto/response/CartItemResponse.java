package io.github.devup.tripfinder.cart.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

// 장바구니 항목 응답 - 숙소, 방 정보도 포함
public record CartItemResponse(
        Long cartItemId,
        Long roomId,
        String roomName,
        String accommodationName,
        BigDecimal price,
        LocalDate checkInDate,
        LocalDate checkOutDate,
        Integer roomQuantity,
        Integer guestCount,
        BigDecimal subtotal
) {
}
