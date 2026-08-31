package io.github.devup.tripfinder.board.repository;

import io.github.devup.tripfinder.board.entity.Board;
import io.github.devup.tripfinder.board.entity.BoardComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardCommentRepository extends JpaRepository<BoardComment,Long> {
    List<BoardComment> findByBoard(Board board); //특정 게시글의 댓글 전체 조회
    void deleteAllByBoard(Board board); //게시글 삭제할 떄 딸린 댓글 같이 지우는 용도
}
