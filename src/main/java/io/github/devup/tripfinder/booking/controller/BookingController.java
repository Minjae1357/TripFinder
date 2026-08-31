package io.github.devup.tripfinder.booking.controller;

import io.github.devup.tripfinder.booking.dto.request.BookingCreateRequest;
import io.github.devup.tripfinder.booking.dto.response.BookingResponse;
import io.github.devup.tripfinder.booking.dto.response.BookingSummaryResponse;
import io.github.devup.tripfinder.booking.exception.BookingAlreadyCanceledException;
import io.github.devup.tripfinder.booking.exception.BookingItemInvalidException;
import io.github.devup.tripfinder.booking.exception.BookingNotFoundException;
import io.github.devup.tripfinder.booking.exception.BookingNotOwnerException;
import io.github.devup.tripfinder.booking.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    // 예약 만들기
    @PostMapping
    public ResponseEntity<Long> createBooking(@RequestBody BookingCreateRequest request){
        Long bookingId = bookingService.createBooking(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingId);
    }
    // 예약 요약 정보
    @GetMapping
    public List<BookingSummaryResponse> getMyBookings(){
        return bookingService.getMyBookings();
    }

    @GetMapping("/{bookingId}")
    public BookingResponse getBooking(@PathVariable Long bookingId){
        return bookingService.getBooking(bookingId);
    }
    // 예약 취소
    @PatchMapping("/{bookingId}/cancel")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long bookingId){
        bookingService.cancelBooking(bookingId);
        return ResponseEntity.noContent().build();
    }
    // 예약 목록에서 삭제
    @DeleteMapping("/{bookingId}")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long bookingId){
        bookingService.deleteBooking(bookingId);
        return ResponseEntity.noContent().build();
    }
    // 예약 확정 이력 체크
    @GetMapping("/check")
    public boolean checkBookingHistory(@RequestParam Long accommodationId){
        return bookingService.hasBookingHistory(accommodationId);
    }
    // 예외 처리
    @ExceptionHandler(BookingNotFoundException.class)
    public ResponseEntity<String> handleBookingNotFound(BookingNotFoundException e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(BookingItemInvalidException.class)
    public ResponseEntity<String> handleBookingItemInvalid(BookingItemInvalidException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(BookingNotOwnerException.class)
    public ResponseEntity<String> handleBookingNotOwner(BookingNotOwnerException e){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    @ExceptionHandler(BookingAlreadyCanceledException.class)
    public ResponseEntity<String> handleBookingAlreadyCanceled(BookingAlreadyCanceledException e){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }
}
