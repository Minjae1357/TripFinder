package io.github.devup.tripfinder.accommodationreview.entity;

import io.github.devup.tripfinder.accommodation.entity.Accommodation;
import io.github.devup.tripfinder.auth.entity.Users;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "ACCOMMODATION_REVIEWS")
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

    // USER구현 후
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id", nullable = false)
//    private Users userid;

    // test용 user entity
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
