package io.github.devup.tripfinder.board.repository;

import io.github.devup.tripfinder.auth.entity.Users;
import io.github.devup.tripfinder.board.entity.Board;
import io.github.devup.tripfinder.board.entity.LikeBoard;
import io.github.devup.tripfinder.board.entity.LikeBoardId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
//PK가 복합키(userid+boardid)라서 <엔티티, 그 복합키를 담는 그릇(LikeBoardId)>로 지정로 지정
public interface LikeBoardRepository extends JpaRepository<LikeBoard, LikeBoardId> {
    boolean existsByUserAndBoard(Users user , Board board); // 있는지 없는지만알려줘서 이미 좋아요 눌렀는지 체크(토글용)
    void deleteByUserAndBoard(Users user, Board board); //좋아요 취소
    long countByBoard(Board board); //게시글의 좋아요 총 개수
    void deleteAllByBoard(Board board);
}
