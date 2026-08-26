package io.github.devup.tripfinder.accommodationreview.repository;

import io.github.devup.tripfinder.accommodationreview.entity.AccommodationReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccommodationReviewRepository extends JpaRepository<AccommodationReview, Long> {
    // 리뷰 전체 조회(쓸지 안쓸지 모름)
    List<AccommodationReview> findByAccommodation_AccommodationId(Long accommodationId);

    // 리뷰 페이징 및 정렬 조회
    Page<AccommodationReview> findByAccommodation_AccommodationId(Long accommodationId, Pageable pageable);

    @Query("SELECT AVG(r.star) FROM AccommodationReview r WHERE r.accommodation.accommodationId = :accommodationId")
    Double findAverageStarByAccommodationId(@Param("accommodationId") Long accommodationId);

    long countByAccommodation_AccommodationId(Long accommodationId);

}
