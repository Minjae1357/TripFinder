package io.github.devup.tripfinder.board.repository;

import io.github.devup.tripfinder.board.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board,Long> {
    List<Board> findByCategory(String category); //카테고리별 조회
    List<Board> findByOrderByCreatedAtDesc(); //전체 목록, 최신순
}
