package io.github.devup.tripfinder.accommodationreview.dto.response;

import java.util.List;

// 리뷰 목록 + 페이징
public record AccommodationReviewPageResponse(
        List<AccommodationReviewResponse> reviews,
        int currentPage,
        int totalPages,
        long totalElements
) {
}
