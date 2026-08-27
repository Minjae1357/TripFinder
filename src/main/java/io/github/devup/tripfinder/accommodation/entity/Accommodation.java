package io.github.devup.tripfinder.accommodation.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Entity
@Table(name = "accommodation")
@Data
@NoArgsConstructor
public class Accommodation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "accommodation_id")
    private Long accommodationId;

    @Column(name = "accommodation_type")
    private String accommodationType;

    @Column(name = "accommodation_name")
    private String accommodationName;

    @Column(name = "region")
    private String region;

    @Column(name = "address")
    private String address;

    @Column(name = "accommodation_lat", columnDefinition = "decimal(10,7)")
    private double accommodationLat;

    @Column(name = "accommodation_lng", columnDefinition = "decimal(10,7)")
    private double accommodationLng;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

}
