package io.github.devup.tripfinder.accommodationreview.dto.response;

// 리뷰 총 개수, 평점
public record AccommodationReviewSummaryResponse(
        double averageStar,
        long reviewCount
) {
}
