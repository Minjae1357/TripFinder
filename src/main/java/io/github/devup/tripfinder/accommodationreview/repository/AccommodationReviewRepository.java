package io.github.devup.tripfinder.accommodationreview.repository;

import io.github.devup.tripfinder.accommodationreview.entity.AccommodationReview;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccommodationReviewRepository extends JpaRepository<AccommodationReview, Long> {
}
