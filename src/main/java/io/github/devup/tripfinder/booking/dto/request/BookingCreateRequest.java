package io.github.devup.tripfinder.booking.dto.request;

import java.util.List;

// 장바구니에 선택한 항목들의 id목록으로 예약 생성
public record BookingCreateRequest(
        List<Long> cartItemIds
) {
}
