package io.github.devup.tripfinder.cart.service;

import io.github.devup.tripfinder.accommodation.entity.Room;
import io.github.devup.tripfinder.accommodation.exception.RoomNotFoundException;
import io.github.devup.tripfinder.accommodation.repository.RoomRepository;
import io.github.devup.tripfinder.cart.dto.request.CartItemAddRequest;
import io.github.devup.tripfinder.cart.dto.request.CartItemUpdateRequest;
import io.github.devup.tripfinder.cart.dto.response.CartItemResponse;
import io.github.devup.tripfinder.cart.dto.response.CartResponse;
import io.github.devup.tripfinder.cart.entity.Cart;
import io.github.devup.tripfinder.cart.entity.CartItem;
import io.github.devup.tripfinder.cart.exception.CartItemNotFoundException;
import io.github.devup.tripfinder.cart.exception.CartItemNotOwnerException;
import io.github.devup.tripfinder.cart.repository.CartItemRepository;
import io.github.devup.tripfinder.cart.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final RoomRepository roomRepository;

    // 로그인 구현 전까지 임시 고정 사용자
    private static final Long TEMP_USER_ID = 1L;

    // 장바구니에 추가
    @Transactional
    public void addItem(CartItemAddRequest request){
        Cart cart = getOrCreateCart();

        Room room = roomRepository.findById(request.roomId()).orElseThrow(() -> new RoomNotFoundException("객실을 찾을 수 없습니다. id = " + request.roomId()));

        // 최대 인원, 체크인아웃 날짜 검증
        validateCartItemRequest(room, request.guestCount(), request.roomQuantity(), request.checkInDate(), request.checkOutDate());

        CartItem item = new CartItem();
        item.setCart(cart);
        item.setRoom(room);
        item.setCheckInDate(request.checkInDate());
        item.setCheckOutDate(request.checkOutDate());
        item.setRoomQuantity(request.roomQuantity());
        item.setGuestCount(request.guestCount());

        cartItemRepository.save(item);
    }

    @Transactional(readOnly = true)
    public CartResponse getCart(){
        return cartRepository.findByUserId(TEMP_USER_ID).map(this::toResponse).orElseGet(()->new CartResponse(null, List.of(), BigDecimal.ZERO));
    }

    @Transactional
    public void updateItem(Long cartItemId, CartItemUpdateRequest request){
        CartItem item = cartItemRepository.findById(cartItemId).orElseThrow(()-> new CartItemNotFoundException("장바구니 항목을 찾을 수 없습니다. id = " + cartItemId));

        validateOwner(item);

        // 최대 인원, 체크인아웃 날짜 검증
        validateCartItemRequest(item.getRoom(), request.guestCount(), request.roomQuantity(), request.checkInDate(), request.checkOutDate());

        item.setCheckInDate(request.checkInDate());
        item.setCheckOutDate(request.checkOutDate());
        item.setRoomQuantity(request.roomQuantity());
        item.setGuestCount(request.guestCount());
    }

    @Transactional
    public void deleteItem(Long cartItemId){
        CartItem item = cartItemRepository.findById(cartItemId).orElseThrow(()-> new CartItemNotFoundException("장바구니 항목을 찾을 수 없습니다. id = " + cartItemId));

        validateOwner(item);
        cartItemRepository.delete(item);
    }

    private Cart getOrCreateCart(){
        return cartRepository.findByUserId(TEMP_USER_ID).orElseGet(()->{
            Cart newCart = new Cart();
            newCart.setUserId(TEMP_USER_ID);
            return cartRepository.save(newCart);
        });
    }

    // 본인 장바구니 항목 확인
    private void validateOwner(CartItem item){
        if(!item.getCart().getUserId().equals(TEMP_USER_ID)){
            throw new CartItemNotOwnerException("본인의 장바구니만 수정/삭제할 수 있습니다."); // 본인 외의 장바구니가 보이는 상황 방지
        }
    }

    private CartResponse toResponse(Cart cart){
        List<CartItem> items = cartItemRepository.findByCart_CartId(cart.getCartId());

        List<CartItemResponse> itemResponses = items.stream().map(this::toItemResponse).toList();

        BigDecimal totalAmount = itemResponses.stream().map(CartItemResponse::subtotal).reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CartResponse(cart.getCartId(), itemResponses, totalAmount);
    }

    // 최대 인원, 체크인아웃 날짜 검증
    private void validateCartItemRequest(Room room, Integer guestCount, Integer roomQuantity, LocalDate checkInDate, LocalDate checkOutDate){
        if(guestCount == null || guestCount < 1 || guestCount >room.getMaxGuest()){
            throw new IllegalArgumentException("최대 인원은 " + room.getMaxGuest() + "명 입니다.");
        }
        if(roomQuantity == null || roomQuantity < 1 || roomQuantity > room.getTotalRoomCount()){
            throw new IllegalArgumentException("남은 객실은 " + room.getTotalRoomCount() + "개 입니다.");
        }
        if(checkInDate == null || checkOutDate == null || !checkInDate.isBefore(checkOutDate)){
            throw new IllegalArgumentException("체크아웃 날짜는 체크인 날짜보다 뒤여야 합니다");
        }
    }

    private CartItemResponse toItemResponse(CartItem item){
        Room room = item.getRoom();

        // 숙박 일 수 계산(체크아웃일 - 체크인일 = n박)
        long nights = ChronoUnit.DAYS.between(item.getCheckInDate(), item.getCheckOutDate());
        BigDecimal subtotal = room.getPrice().multiply(BigDecimal.valueOf(item.getRoomQuantity())).multiply(BigDecimal.valueOf(nights));

        return new CartItemResponse(
                item.getCartItemId(),
                room.getRoomId(),
                room.getRoomName(),
                room.getAccommodation().getAccommodationName(),
                room.getPrice(),
                item.getCheckInDate(),
                item.getCheckOutDate(),
                item.getRoomQuantity(),
                item.getGuestCount(),
                subtotal
        );
    }
}

