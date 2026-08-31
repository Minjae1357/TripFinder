package io.github.devup.tripfinder.accommodationreview.controller;

import io.github.devup.tripfinder.accommodationreview.dto.request.AccommodationReviewCreateRequest;
import io.github.devup.tripfinder.accommodationreview.dto.request.AccommodationReviewUpdateRequest;
import io.github.devup.tripfinder.accommodationreview.dto.response.AccommodationReviewPageResponse;
import io.github.devup.tripfinder.accommodationreview.dto.response.AccommodationReviewResponse;
import io.github.devup.tripfinder.accommodationreview.dto.response.AccommodationReviewSummaryResponse;
import io.github.devup.tripfinder.accommodationreview.exception.AccommodationReviewEditCooldownException;
import io.github.devup.tripfinder.accommodationreview.exception.AccommodationReviewNotEligibleException;
import io.github.devup.tripfinder.accommodationreview.exception.AccommodationReviewNotOwnerException;
import io.github.devup.tripfinder.accommodationreview.service.AccommodationReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    // 리뷰 생성
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Long> createReview(
            @PathVariable Long accommodationId,
            @RequestPart("review")AccommodationReviewCreateRequest request, // JSON 데이터
            @RequestPart(value = "images", required = false) List<MultipartFile> images // 파일
    ){
        Long reviewId = accommodationReviewService.createReview(accommodationId, request, images);
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewId);
    }

    // 리뷰 수정
    @PatchMapping("/{reviewId}")
    public ResponseEntity<Void>  updateReview(
            @PathVariable Long accommodationId,
            @PathVariable Long reviewId,
            @RequestBody AccommodationReviewUpdateRequest request
            ){
        accommodationReviewService.updateReview(reviewId, request);
        return ResponseEntity.noContent().build();
    }

    // 리뷰 삭제
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void>  deleteReview(
            @PathVariable Long accommodationId,
            @PathVariable Long reviewId
    ){
        accommodationReviewService.deleteReview(reviewId);
        return ResponseEntity.noContent().build();
    }

    // 리뷰 한건 조회
    @GetMapping("/{reviewId}")
    public AccommodationReviewResponse getReview(
            @PathVariable Long accommodationId,
            @PathVariable Long reviewId
    ){
        return accommodationReviewService.getReview(reviewId);
    }

    // 예외처리
    @ExceptionHandler(AccommodationReviewEditCooldownException.class)
    public ResponseEntity<String> handleAccommodationReviewEditCooldown(AccommodationReviewEditCooldownException e){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    @ExceptionHandler(AccommodationReviewNotEligibleException.class)
    public ResponseEntity<String> handleAccommodationReviewNotEligible(AccommodationReviewNotEligibleException e){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    @ExceptionHandler(AccommodationReviewNotOwnerException.class)
    public ResponseEntity<String> handleAccommodationReviewNotOwner(AccommodationReviewNotOwnerException e){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    // 리뷰 전체 조회(일단 안씀)
//    @GetMapping
//    public List<AccommodationReviewResponse> getReviews(@PathVariable Long accommodationId) {
//        return accommodationReviewService.getReviews(accommodationId);
//    }
}
