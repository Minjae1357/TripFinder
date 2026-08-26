package io.github.devup.tripfinder.accommodationreview.controller;

import io.github.devup.tripfinder.accommodationreview.dto.response.AccommodationReviewPageResponse;
import io.github.devup.tripfinder.accommodationreview.dto.response.AccommodationReviewResponse;
import io.github.devup.tripfinder.accommodationreview.dto.response.AccommodationReviewSummaryResponse;
import io.github.devup.tripfinder.accommodationreview.service.AccommodationReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accommodations/{accommodationId}/reviews")
@RequiredArgsConstructor
public class AccommodationReviewController {
    private final AccommodationReviewService accommodationReviewService;

    // 리뷰 페이징 정렬 조회
    @GetMapping
    public AccommodationReviewPageResponse getReviews(
            @PathVariable Long accommodationId,
            @RequestParam(defaultValue = "recommend") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        return accommodationReviewService.getReviews(accommodationId, sort, page, size);
    }

    // 리뷰 평점, 총 개수
    @GetMapping("/summary")
    public AccommodationReviewSummaryResponse getSummery(@PathVariable Long accommodationId) {
        return accommodationReviewService.getReviewSummary(accommodationId);
    }


    // 리뷰 전체 조회(일단 안씀)
//    @GetMapping
//    public List<AccommodationReviewResponse> getReviews(@PathVariable Long accommodationId) {
//        return accommodationReviewService.getReviews(accommodationId);
//    }
}
