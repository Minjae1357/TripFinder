package io.github.devup.tripfinder.cart.dto.request;

import java.time.LocalDate;

// 장바구니에 방 담기 요청
public record CartItemAddRequest(
        Long roomId,
        LocalDate checkInDate,
        LocalDate checkOutDate,
        Integer roomQuantity,
        Integer guestCount
) {
}
