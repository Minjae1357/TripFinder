package io.github.devup.tripfinder.booking.repository;

import io.github.devup.tripfinder.booking.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserIdOrderByCreatedAtDesc(Long id);

    // 특정 유저가 특정 숙소를 예약 확정 이력이 있는지 확인
    @Query("""
        SELECT COUNT(b) > 0 FROM Booking b
        JOIN BookingItem bi ON bi.booking = b
        WHERE b.userId = :userId
        AND bi.room.accommodation.accommodationId = :accommodationId
        And b.bookingStatus = 'CONFIRMED'
    """)
    boolean existsConfirmedBookingByUserAndAccommodation(
            @Param("userId") Long userId,
            @Param("accommodationId") Long accommodationId
    );
}
