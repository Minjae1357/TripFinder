package io.github.devup.tripfinder.accommodation.repository;

import io.github.devup.tripfinder.accommodation.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room,Long> {
    List<Room> findByAccommodation_AccommodationId(Long accommodationId);
}
