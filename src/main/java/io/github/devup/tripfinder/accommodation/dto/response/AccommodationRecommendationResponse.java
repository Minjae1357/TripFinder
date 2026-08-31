package io.github.devup.tripfinder.accommodation.dto.response;

public record AccommodationRecommendationResponse(
        Long accommodationId,
        String accommodationName,
        String accommodationType,
        String region,
        double accommodationLat,
        double accommodationLng,
        double averageStar,
        long reviewCount
) {
}
