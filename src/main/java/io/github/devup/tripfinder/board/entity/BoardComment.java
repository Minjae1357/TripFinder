package io.github.devup.tripfinder.board.entity;

import io.github.devup.tripfinder.auth.entity.Users;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name= "board_comment")
public class BoardComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    // 댓글 - > 게시글 (이 댓글이 어느글에 달렸나)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="board_id",nullable = false)
    private Board board;

    // 댓글 -> 작성자 (이댓글을 누가썼나)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id",nullable = false)
    private Users user;

    // 댓글 -> 부모댓글( 대댓글이면 원댓글이 뭔가) 셀프조인
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="parent_id", nullable = false) //일반 댓글이면 null, 대댓글이면 부모 댓글의 id
    private BoardComment parent;

    @Column(nullable = false, length = 1000)
    private String contents;

    @CreatedDate
    @Column(name ="created_at" , nullable = false , updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name="updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public BoardComment(Board board, Users user, BoardComment parent , String contents){
        this.board = board;
        this.user =user;
        this.parent = parent; //일반댓글 남김면 null로 넘겨야함
        this.contents = contents;
    }

    //댓글수정용
    public void updateContents(String contents){
        this.contents = contents;
    }

    //대댓글인지 확인하느 편의 메서드(parent가있어야 대댓글)
    public boolean isReply(){
        return this.parent != null;
    }

}
