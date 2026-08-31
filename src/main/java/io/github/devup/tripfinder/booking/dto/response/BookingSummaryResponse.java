package io.github.devup.tripfinder.booking.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// 예약 목록 조회용 - 항목 상세 없이 요약 정보만
public record BookingSummaryResponse(
        Long bookingId,
        String bookingStatus,
        BigDecimal totalAmount,
        LocalDateTime createdAt
) {
}
