package io.github.devup.tripfinder.accommodation.repository;

import io.github.devup.tripfinder.accommodation.entity.RoomImg;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomImgRepository extends JpaRepository<RoomImg,Long> {
    List<RoomImg> findByRoom_RoomIdOrderByImgOrder(Long roomId);
}
