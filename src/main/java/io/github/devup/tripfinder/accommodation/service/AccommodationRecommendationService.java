package io.github.devup.tripfinder.accommodation.service;

import io.github.devup.tripfinder.accommodation.dto.response.AccommodationRecommendationResponse;
import io.github.devup.tripfinder.accommodation.entity.Accommodation;
import io.github.devup.tripfinder.accommodation.repository.AccommodationRepository;
import io.github.devup.tripfinder.accommodationreview.dto.response.AccommodationReviewResponse;
import io.github.devup.tripfinder.accommodationreview.repository.AccommodationReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccommodationRecommendationService {
    private final AccommodationRepository accommodationRepository;
    private final AccommodationReviewRepository accommodationReviewRepository;

    private static final int CANDIDATE_POOL_SIZE = 10;  // 평점 상위 n개를 먼저 뽑음(현재는 10개)
    private static final int RECOMMEND_COUNT = 5;   // 최종 추천 개수

    @Transactional(readOnly = true)
    public List<AccommodationRecommendationResponse> recommendByRegion(String region) {
        List<Accommodation> accommodations = accommodationRepository.findByRegion(region);

        if(accommodations.isEmpty()){
            return List.of();
        }

        // 각 숙소의 평균 평점/리뷰 수를 계산해서 DTO로 변환
        List<AccommodationRecommendationResponse> candidates = accommodations.stream()
                .map(this::toReccomendationResponse)
                .sorted(Comparator.comparingDouble(AccommodationRecommendationResponse::averageStar).reversed())
                .toList();

        // 평점 상위 n개만 후보군으로(숙소 총 개수가 n보다 적으면 전체)
        List<AccommodationRecommendationResponse> pool = new ArrayList<>(
                candidates.subList(0, Math.min(CANDIDATE_POOL_SIZE, candidates.size()))
        );

        // 후보군 랜덤 셔플
        Collections.shuffle(pool);

        // 셔플 후 원하는 개수만큼 추출
        int resultCount = Math.min(RECOMMEND_COUNT, pool.size());
        return pool.subList(0, resultCount);
    }

    private AccommodationRecommendationResponse toReccomendationResponse(Accommodation accommodation){
        Double avg = accommodationReviewRepository.findAverageStarByAccommodationId(accommodation.getAccommodationId());
        long count = accommodationReviewRepository.countByAccommodation_AccommodationId(accommodation.getAccommodationId());

        return new AccommodationRecommendationResponse(
                accommodation.getAccommodationId(),
                accommodation.getAccommodationName(),
                accommodation.getAccommodationType(),
                accommodation.getRegion(),
                accommodation.getAccommodationLat(),
                accommodation.getAccommodationLng(),
                avg != null ? avg : 0.0,
                count
        );
    }
}
