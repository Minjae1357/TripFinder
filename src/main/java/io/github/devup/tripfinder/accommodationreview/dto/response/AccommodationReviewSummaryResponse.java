package io.github.devup.tripfinder.accommodationreview.dto.response;

// 리뷰 총 개수, 평점 ( 프론트에서 맞출것 )
public record AccommodationReviewSummaryResponse(
        double averageStar,
        long reviewCount
) {
}
