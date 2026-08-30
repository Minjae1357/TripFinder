package io.github.devup.tripfinder.board.repository;

import io.github.devup.tripfinder.board.entity.Board;
import io.github.devup.tripfinder.board.entity.BoardImg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardImgRepository extends JpaRepository<BoardImg,Long> {
    List<BoardImg> findAllByBoardOrderByImgOrder(Board board); //특정 게시글의 사진들, 순서대로
    void deleteAllByBoard(Board board); //게시글지울떄 사용할거
}
