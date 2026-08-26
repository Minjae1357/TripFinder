package io.github.devup.tripfinder.accommodationreview.repository;

import io.github.devup.tripfinder.accommodationreview.entity.AccommodationReviewImg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccommodationReviewImgRepository extends JpaRepository<AccommodationReviewImg, Long> {
    List<AccommodationReviewImg> findByReview_ReviewIdOrderByImgOrder(Long reviewId);
}
