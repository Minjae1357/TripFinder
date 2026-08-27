package io.github.devup.tripfinder.accommodationreview.dto.request;

public record AccommodationReviewCreateRequest(
        Byte star,
        String reviewContents
) {
}
