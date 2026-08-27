package io.github.devup.tripfinder.accommodationreview.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "accommodation_review_img")
@Data
@NoArgsConstructor
public class AccommodationReviewImg {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private AccommodationReview review;

    @Column(name = "img_url")
    private String imgUrl;

    @Column(name = "img_order")
    private Integer imgOrder;

}
