package io.github.devup.tripfinder.accommodation.dto.response;

import java.util.List;

// 숙소 상세 정보 Response
public record AccommodationDetailResponse(
        Long accommodationId,
        String accommodationName,
        String accommodationType,
        String region,
        String address,
        double accommodationLat,
        double accommodationLng,
        List<RoomResponse> rooms
) {
}
