package io.github.devup.tripfinder.booking.repository;

import io.github.devup.tripfinder.booking.entity.BookingItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingItemRepository extends JpaRepository<BookingItem, Long> {
    List<BookingItem> findByBooking_BookingId(Long bookingId);
}
