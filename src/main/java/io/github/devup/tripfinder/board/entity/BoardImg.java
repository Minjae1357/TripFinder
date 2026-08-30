package io.github.devup.tripfinder.board.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "board_img")
public class BoardImg {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // 이 사진이 어느 게시글에 첨부됫는지확인
    @JoinColumn(name = "board_id")
    private Board board;

    @Column(name = "img_url", nullable = false, length = 500)
    private String imgUrl;

    @Column(name="img_order", nullable = false)
    private Integer imgOrder;

    @Builder
    public BoardImg(Board board, String imgUrl, Integer imgOrder) {
        this.board = board;
        this.imgUrl = imgUrl;
        this.imgOrder = imgOrder;
    }
}
