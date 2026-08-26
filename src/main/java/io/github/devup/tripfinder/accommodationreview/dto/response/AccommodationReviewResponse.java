package io.github.devup.tripfinder.accommodationreview.dto.response;

import java.time.LocalDateTime;
import java.util.List;


public record AccommodationReviewResponse(
        Long reviewId,
        Long userId,
        Byte star,
        String reviewContents,
        LocalDateTime createdAt,
        List<String> reviewImgUrls

) {
}
