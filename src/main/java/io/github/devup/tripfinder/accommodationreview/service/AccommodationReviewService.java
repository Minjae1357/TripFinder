package io.github.devup.tripfinder.accommodationreview.service;

import io.github.devup.tripfinder.accommodation.repository.AccommodationRepository;
import io.github.devup.tripfinder.accommodationreview.dto.request.AccommodationReviewCreateRequest;
import io.github.devup.tripfinder.accommodation.entity.Accommodation;
import io.github.devup.tripfinder.accommodationreview.dto.request.AccommodationReviewUpdateRequest;
import io.github.devup.tripfinder.accommodationreview.dto.response.AccommodationReviewPageResponse;
import io.github.devup.tripfinder.accommodationreview.dto.response.AccommodationReviewResponse;
import io.github.devup.tripfinder.accommodationreview.dto.response.AccommodationReviewSummaryResponse;
import io.github.devup.tripfinder.accommodationreview.entity.AccommodationReview;
import io.github.devup.tripfinder.accommodationreview.entity.AccommodationReviewImg;
import io.github.devup.tripfinder.accommodationreview.exception.AccommodationReviewEditCooldownException;
import io.github.devup.tripfinder.accommodationreview.exception.AccommodationReviewNotEligibleException;
import io.github.devup.tripfinder.accommodationreview.exception.AccommodationReviewNotOwnerException;
import io.github.devup.tripfinder.accommodationreview.repository.AccommodationReviewImgRepository;
import io.github.devup.tripfinder.accommodationreview.repository.AccommodationReviewRepository;
import io.github.devup.tripfinder.booking.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccommodationReviewService {
    private final AccommodationReviewRepository accommodationReviewRepository;
    private final AccommodationReviewImgRepository accommodationReviewImgRepository;
    private final AccommodationRepository accommodationRepository;
    private final FileStorageService fileStorageService;

    private final BookingRepository bookingRepository;

    // 리뷰 쓰기
    @Transactional
    public Long createReview(Long userId, Long accommodationId, AccommodationReviewCreateRequest request, List<MultipartFile> images) {

        if(!bookingRepository.existsConfirmedBookingByUserAndAccommodation(userId, accommodationId)){
            throw new AccommodationReviewNotEligibleException("예약이 확정된 숙소에만 리뷰를 작성할 수 있습니다.");
        }

        Accommodation accommodation = accommodationRepository.findById(accommodationId).orElseThrow(()-> new IllegalArgumentException("숙소를 찾을 수 없습니다. id=" + accommodationId));

        AccommodationReview review = new AccommodationReview();
        review.setAccommodation(accommodation);
        review.setUserId(userId);
        review.setStar(request.star());
        review.setReviewContents(request.reviewContents());

        AccommodationReview savedReview = accommodationReviewRepository.save(review);

        // 이미지가 있을떄만 저장( 사진 없이 리뷰 쓸 가능성 )
        if(images != null && !images.isEmpty()) {
            int order = 1;
            for(MultipartFile image : images) {
                if(image.isEmpty()) continue; // 빈 파일 건너뛰기

                String imgUrl = fileStorageService.store(image); // 디스크에 저장후 URL 받아옴

                AccommodationReviewImg reviewImg = new AccommodationReviewImg();
                reviewImg.setReview(savedReview);
                reviewImg.setImgUrl(imgUrl);
                reviewImg.setImgOrder(order++);
                accommodationReviewImgRepository.save(reviewImg);
            }
        }
        return savedReview.getReviewId();
    }

    // 리뷰 수정
    @Transactional
    public void updateReview(Long userId, Long reviewId, AccommodationReviewUpdateRequest request) {
        AccommodationReview review = accommodationReviewRepository.findById(reviewId).orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다. ID = "+reviewId));

        // 본인 확인
        validateOwner(userId, review);
        validateEditCooldown(review); // 리뷰 수정 10초 제한 추가

        review.setStar(request.star());
        review.setReviewContents(request.reviewContents());
    }

    private static final long EDIT_COOLDOWN_SECONDS =10; // 마지막 수정, 등록 후 10초가 지나지 않았으면 예외 발생

    // 수정, 등록 10초 이내 수정시 던지는 예외 처리
    private void validateEditCooldown(AccommodationReview review) {
        LocalDateTime lastModified = review.getUpdatedAt();
        long secondsSinceLastEdit = Duration.between(lastModified, LocalDateTime.now()).getSeconds();

        if(secondsSinceLastEdit < EDIT_COOLDOWN_SECONDS){
            long remainingSeconds = EDIT_COOLDOWN_SECONDS - secondsSinceLastEdit;
            throw new AccommodationReviewEditCooldownException(remainingSeconds + "초 후에 다시 시도해주세요.");
        }
    }

    // 리뷰 삭제
    @Transactional
    public void deleteReview(Long userId, Long reviewId) {
        AccommodationReview review = accommodationReviewRepository.findById(reviewId).orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 업습니다. ID = "+reviewId));

        validateOwner(userId, review);

        accommodationReviewImgRepository.deleteAll(accommodationReviewImgRepository.findByReview_ReviewIdOrderByImgOrder(reviewId));
        accommodationReviewRepository.delete(review);
    }

    // 작성자 검증
    private void validateOwner(Long userId, AccommodationReview review) {
        if(!review.getUserId().equals(userId)) {
            throw new AccommodationReviewNotOwnerException("본인이 작성한 리뷰만 수정/삭제할 수 있습니다.");
        }
    }

    // 리뷰 하나만 검색
    @Transactional(readOnly = true)
    public AccommodationReviewResponse getReview(Long reviewId) {
        AccommodationReview review = accommodationReviewRepository.findById(reviewId).orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다. id = "+reviewId));
        return toResponse(review);
    }


    // 리뷰 페이징
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
                review.getUpdatedAt(),
                reviewImgUrls
        );
    }
}

