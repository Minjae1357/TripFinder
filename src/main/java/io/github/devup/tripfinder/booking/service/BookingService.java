package io.github.devup.tripfinder.booking.service;

import io.github.devup.tripfinder.booking.dto.request.BookingCreateRequest;
import io.github.devup.tripfinder.booking.dto.response.BookingItemResponse;
import io.github.devup.tripfinder.booking.dto.response.BookingResponse;
import io.github.devup.tripfinder.booking.dto.response.BookingSummaryResponse;
import io.github.devup.tripfinder.booking.entity.Booking;
import io.github.devup.tripfinder.booking.entity.BookingItem;
import io.github.devup.tripfinder.booking.exception.BookingAlreadyCanceledException;
import io.github.devup.tripfinder.booking.exception.BookingItemInvalidException;
import io.github.devup.tripfinder.booking.exception.BookingNotFoundException;
import io.github.devup.tripfinder.booking.exception.BookingNotOwnerException;
import io.github.devup.tripfinder.booking.repository.BookingItemRepository;
import io.github.devup.tripfinder.booking.repository.BookingRepository;
import io.github.devup.tripfinder.cart.entity.CartItem;
import io.github.devup.tripfinder.cart.repository.CartItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final BookingItemRepository bookingItemRepository;
    private final CartItemRepository cartItemRepository;

    // 장바구니에서 선택한 항목들로 예약 설정 - 예약 완료 후 해당 장바구니 항목은 삭제
    @Transactional
    public Long createBooking(Long userId, BookingCreateRequest request){
        if(request.cartItemIds() == null || request.cartItemIds().isEmpty()){
            throw new BookingItemInvalidException("예약할 항목을 선택해주세요.");
        }

        List<CartItem> cartItems = cartItemRepository.findAllById(request.cartItemIds());

        if(cartItems.size() != request.cartItemIds().size()){
            throw new BookingItemInvalidException("존재하지 않는 장바구니 항목이 포함되어 있습니다.");
        }

        // 선택한 항목이 전부 본인 장바구니 소속(?)인지 확인(다른 사람 항목 섞이는거 방지)
        for(CartItem cartItem : cartItems){
            if(!cartItem.getCart().getUserId().equals(userId)){
                throw new BookingNotOwnerException("본인의 장바구니 항목만 예약할 수 있습니다.");
            }
        }

        // 예약 먼저 생성 - 합계 금액은 모든 항목 만든 후 채우기
        Booking booking = new Booking();
        booking.setUserId(userId);
        booking.setBookingStatus("CONFIRMED"); // 결제 기능 구현 시 "PENDING"으로 생성 후 결제 성공시 전환
        booking.setTotalAmount(BigDecimal.ZERO); // 아래에서 재계산 후 갱신
        Booking savedBooking = bookingRepository.save(booking);

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItem cartItem : cartItems) {
            long nights = ChronoUnit.DAYS.between(cartItem.getCheckInDate(), cartItem.getCheckOutDate());
            BigDecimal unitPrice = cartItem.getRoom().getPrice(); // 현재 시점 방 가격을 스냅샷으로 저장(방 가격 변동 방지)
            BigDecimal subtotal = unitPrice
                    .multiply(BigDecimal.valueOf(cartItem.getRoomQuantity()))
                    .multiply(BigDecimal.valueOf(nights));

            BookingItem bookingItem = new BookingItem();
            bookingItem.setBooking(savedBooking);
            bookingItem.setRoom(cartItem.getRoom());
            bookingItem.setCheckInDate(cartItem.getCheckInDate());
            bookingItem.setCheckOutDate(cartItem.getCheckOutDate());
            bookingItem.setRoomQuantity(cartItem.getRoomQuantity());
            bookingItem.setGuestCount(cartItem.getGuestCount());
            bookingItem.setUnitPrice(unitPrice);
            bookingItem.setSubtotal(subtotal);
            bookingItemRepository.save(bookingItem);

            totalAmount = totalAmount.add(subtotal);
        }
        savedBooking.setTotalAmount(totalAmount);

        // 예약 완료된 항목은 장바구니에서 제거
        cartItemRepository.deleteAll(cartItems);
        return savedBooking.getBookingId();
    }

    // 예약 상세 조회
    @Transactional(readOnly = true)
    public BookingResponse getBooking(Long userId, Long bookingId){
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new BookingNotFoundException("예약을 찾을 수 없습니다. id = " + bookingId));

        validateOwner(userId, booking);

        List<BookingItemResponse> items = bookingItemRepository.findByBooking_BookingId(bookingId).stream().map(this::toItemResponse).toList();

        return new BookingResponse(
                booking.getBookingId(),
                booking.getBookingStatus(),
                booking.getTotalAmount(),
                booking.getCreatedAt(),
                items
        );
    }

    // 내 예약 목록 조회 (최신순)
    @Transactional(readOnly = true)
    public List<BookingSummaryResponse> getMyBookings(Long userId){
        return bookingRepository.findByUserIdOrderByCreatedAtDesc(userId).stream().map(booking -> new BookingSummaryResponse(
                booking.getBookingId(), booking.getBookingStatus(), booking.getTotalAmount(), booking.getCreatedAt()
        )).toList();
    }

    // 예약 취소
    @Transactional
    public void cancelBooking(Long userId, Long bookingId){
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new BookingNotFoundException("예약을 찾을 수 없습니다. id = " + bookingId));

        validateOwner(userId, booking);

        if("CANCELED".equals(booking.getBookingStatus())){
            throw new BookingAlreadyCanceledException("이미 취소된 예약입니다.");
        }

        booking.setBookingStatus("CANCELED");
    }

    // 예약 목록에서 삭제
    @Transactional
    public void deleteBooking(Long userId, Long bookingId){
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new BookingNotFoundException("예약을 찾을 수 없습니다. id = " + bookingId));

        validateOwner(userId, booking);

        List<BookingItem> items = bookingItemRepository.findByBooking_BookingId(bookingId);
        bookingItemRepository.deleteAll(items); // 자식 먼저 삭제
        bookingRepository.delete(booking);
    }

    // 본인 확인
    private void validateOwner(Long userId, Booking booking){
        if(!booking.getUserId().equals(userId)){
            throw new BookingNotOwnerException("본인의 예약만 조회/취소할 수 있습니다.");
        }
    }

    // 예약 확정 이력
    @Transactional(readOnly = true)
    public boolean hasBookingHistory(Long userId, Long accommodationId){
        return bookingRepository.existsConfirmedBookingByUserAndAccommodation(userId, accommodationId);
    }

    private BookingItemResponse toItemResponse(BookingItem item){
        return new BookingItemResponse(
                item.getBookingItemId(),
                item.getRoom().getRoomName(),
                item.getRoom().getAccommodation().getAccommodationName(),
                item.getCheckInDate(),
                item.getCheckOutDate(),
                item.getRoomQuantity(),
                item.getGuestCount(),
                item.getUnitPrice(),
                item.getSubtotal()
        );
    }
}
