package io.github.devup.tripfinder.accommodationreview.dto.request;

public record AccommodationReviewUpdateRequest (
        Byte star,
        String reviewContents
){
}
