package io.github.devup.tripfinder.accommodationreview.entity;

import io.github.devup.tripfinder.accommodation.entity.Accommodation;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "accommodation_reviews")
@Data
@NoArgsConstructor
public class AccommodationReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long reviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accommodation_id", nullable = false)
    private Accommodation accommodation;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "star")
    private Byte star;

    @Column(name = "review_contents", length = 1000)
    private String reviewContents;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

}
