package io.github.devup.tripfinder.accommodationreview.service;

import io.github.devup.tripfinder.accommodation.service.AccommodationService;
import io.github.devup.tripfinder.accommodationreview.dto.response.AccommodationReviewPageResponse;
import io.github.devup.tripfinder.accommodationreview.dto.response.AccommodationReviewResponse;
import io.github.devup.tripfinder.accommodationreview.dto.response.AccommodationReviewSummaryResponse;
import io.github.devup.tripfinder.accommodationreview.entity.AccommodationReview;
import io.github.devup.tripfinder.accommodationreview.entity.AccommodationReviewImg;
import io.github.devup.tripfinder.accommodationreview.repository.AccommodationReviewImgRepository;
import io.github.devup.tripfinder.accommodationreview.repository.AccommodationReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccommodationReviewService {
    private final AccommodationReviewRepository accommodationReviewRepository;
    private final AccommodationReviewImgRepository accommodationReviewImgRepository;


    @Transactional(readOnly = true)
    public AccommodationReviewPageResponse getReviews(Long accommodationId, String sort, int page, int size){

        // 정렬 옵션 JPA Sort 객체로 변환
        Sort sortOption = switch (sort){
            case "star_desc" -> Sort.by(Sort.Direction.DESC, "star");
            case "star_asc" -> Sort.by(Sort.Direction.ASC, "star");
            case "latest" -> Sort.by(Sort.Direction.DESC, "createdAt");
            default -> Sort.by(Sort.Order.desc("star"), Sort.Order.desc("createdAt"));
        };

        // 페이지, 개수, 정렬방식 객체
        Pageable pageable = PageRequest.of(page, size, sortOption);

        // 정렬 객체 Repository 전달 후 페이징,정렬 결과 Page로
        Page<AccommodationReview> reviewPage = accommodationReviewRepository.findByAccommodation_AccommodationId(accommodationId, pageable);

        // Page 데이터 리스트 꺼내기
        List<AccommodationReviewResponse> reviews = reviewPage.getContent().stream().map(this::toResponse).toList();

        return new AccommodationReviewPageResponse(
                reviews,
                reviewPage.getNumber(),         // 현재 페이지 번호
                reviewPage.getTotalPages(),     // 전체 페이지 수
                reviewPage.getTotalElements()   // 전체 리뷰 개수
        );
    }

    // 리뷰 별점 평균, 리뷰 총 개수
    @Transactional(readOnly = true)
    public AccommodationReviewSummaryResponse getReviewSummary(Long accommodationId){
        Double avg = accommodationReviewRepository.findAverageStarByAccommodationId(accommodationId);
        long count = accommodationReviewRepository.countByAccommodation_AccommodationId(accommodationId);
        return new AccommodationReviewSummaryResponse(avg != null ? avg : 0.0 , count); // 리뷰 0개 일시 avg = null 예외처리
    }

    // 리뷰 전체 조회
    @Transactional(readOnly = true)
    public List<AccommodationReviewResponse> getReviews(Long accommodationId){
        List<AccommodationReview> reviews = accommodationReviewRepository.findByAccommodation_AccommodationId(accommodationId);

        return reviews.stream().map(this::toResponse).toList();
    }

    private AccommodationReviewResponse toResponse(AccommodationReview review){
        List<String> reviewImgUrls = accommodationReviewImgRepository.findByReview_ReviewIdOrderByImgOrder(review.getReviewId())
                .stream()
                .map(AccommodationReviewImg::getImgUrl)
                .toList();

        return new AccommodationReviewResponse(
                review.getReviewId(),
                review.getUserId(),
                review.getStar(),
                review.getReviewContents(),
                review.getCreatedAt(),
                reviewImgUrls
        );
    }
}

