package io.github.devup.tripfinder.accommodation.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "room")
@Data
@NoArgsConstructor
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Long roomId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accommodation_id" ,nullable = false)
    private Accommodation accommodation;

    @Column(name = "room_name")
    private String roomName;

    @Column(name = "price", columnDefinition = "decimal(12,0)")
    private BigDecimal price;

    @Column(name = "total_room_count")
    private Integer totalRoomCount;

    @Column(name = "max_guest")
    private Integer maxGuest;

    @Column(name = "has_spa")
    private Boolean hasSpa;

    @Column(name = "contents")
    private String contents;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;
}
