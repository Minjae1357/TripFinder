package io.github.devup.tripfinder.board.entity;
import io.github.devup.tripfinder.auth.entity.Users;
import jakarta.persistence.*;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class) // 시간 관련된거 자동저장기능쓰려면필요한거
@Table(name = "like_board")
@IdClass(LikeBoard.class) //복합키 클래스에 연결하기
public class LikeBoard {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="board_id")
    private Board board;

    @CreatedDate //좋아요 누른 시점
    @Column(name="created_at" , nullable = false , updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public LikeBoard(Users user , Board board){
        this.user = user;
        this.board = board;
    }


}
