package io.github.devup.tripfinder.accommodation.dto.response;

import java.math.BigDecimal;
import java.util.List;

// 방 정보 Response
public record RoomResponse(
        Long roomId,
        String roomName,
        BigDecimal price,
        Integer maxGuest,
        Integer totalRoomCount,
        Boolean hasSpa,
        String contents,
        List<String> imgUrls
) {
}
