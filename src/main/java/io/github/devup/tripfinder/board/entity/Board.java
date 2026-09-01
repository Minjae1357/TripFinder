package io.github.devup.tripfinder.board.entity;

import io.github.devup.tripfinder.auth.entity.Users;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class) //at들 만든시간 업데이트시간 기록하려면 필요한거
@Table(name = "board")
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) //이거 사용하면 여기에 users가 담김
    @JoinColumn(name = "user_id", nullable = false) //sql의 user_id kf 컬럼과 매핑시킴
    private Users user;

    @Column(nullable = false, length = 20)
    private String category;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false)
    private String contents;

    @Column(nullable = false)
    @ColumnDefault("0") // sql : hit 기본값이랑동일하게
    private Integer hit = 0;

    @CreatedDate //insert 시점에 자동으로 들어감
    @Column(name ="created_at" , nullable = false, updatable = false) //updatable=false → 이후 수정 불가
    private LocalDateTime createdAt;

    @LastModifiedDate // update 될떄 자동 갱신
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "board", fetch = FetchType.LAZY) //boardimg.board 필드 기준으로 역방향 연결
    private final List<BoardImg> boardImgs = new ArrayList<>();

    @Builder
    public Board(Users user, String category , String title, String contents){
        this.user = user;
        this.category = category;
        this.title = title;
        this.contents = contents;
        this.hit = 0; //생성할떄 무조건 0으로
    }

    public void increaseHit(){
        this.hit++;
    }

    public void update(String title, String contents){
        this.title = title;
        this.contents =contents;
    }
}
