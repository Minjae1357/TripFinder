package io.github.devup.tripfinder.accommodation.dto.response;

// 지도 마커에 표시할 숙소 간략 정보 Response
public record AccommodationListResponse(
        Long accommodationId,
        String accommodationType,
        String accommodationName,
        double accommodationLat,
        double accommodationLng
) {
}
