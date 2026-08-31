package io.github.devup.tripfinder.cart.dto.request;

import java.time.LocalDate;

// 장바구니 항목 수정 요청(날짜,수량,인원 변경)
public record CartItemUpdateRequest(
        LocalDate checkInDate,
        LocalDate checkOutDate,
        Integer roomQuantity,
        Integer guestCount
) {
}
