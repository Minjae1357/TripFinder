package io.github.devup.tripfinder.accommodation.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ROOM_IMG")
@Data
@NoArgsConstructor
public class RoomImg {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(name = "img_url")
    private String imgUrl;

    @Column(name = "img_order")
    private Integer imgOrder;
}
