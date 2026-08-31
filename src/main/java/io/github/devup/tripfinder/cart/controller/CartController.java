package io.github.devup.tripfinder.cart.controller;

import io.github.devup.tripfinder.accommodation.exception.RoomNotFoundException;
import io.github.devup.tripfinder.cart.dto.request.CartItemAddRequest;
import io.github.devup.tripfinder.cart.dto.request.CartItemUpdateRequest;
import io.github.devup.tripfinder.cart.dto.response.CartResponse;
import io.github.devup.tripfinder.cart.exception.CartItemNotFoundException;
import io.github.devup.tripfinder.cart.exception.CartItemNotOwnerException;
import io.github.devup.tripfinder.cart.service.CartService;
import io.github.devup.tripfinder.common.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    // 내 장바구니 조회
    @GetMapping
    public CartResponse getCart() {
        return cartService.getCart(SecurityUtil.getCurrentUserId());
    }

    // 방 담기
    @PostMapping("/items")
    public ResponseEntity<Void> addItem(@RequestBody CartItemAddRequest request){
        cartService.addItem(SecurityUtil.getCurrentUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 날짜/수량/인원 수정
    @PatchMapping("/items/{cartItemId}")
    public ResponseEntity<Void> updateItem(@PathVariable Long cartItemId, @RequestBody CartItemUpdateRequest request){
        cartService.updateItem(SecurityUtil.getCurrentUserId(), cartItemId, request);
        return ResponseEntity.noContent().build();
    }

    // 항목 삭제
    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long cartItemId){
        cartService.deleteItem(SecurityUtil.getCurrentUserId(), cartItemId);
        return ResponseEntity.noContent().build();
    }

    // 예외처리
    @ExceptionHandler(CartItemNotOwnerException.class)
    public ResponseEntity<String> handleCartItemNotOwner(CartItemNotOwnerException e){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    @ExceptionHandler(RoomNotFoundException.class)
    public ResponseEntity<String> handleRoomNotFound(RoomNotFoundException e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(CartItemNotFoundException.class)
    public ResponseEntity<String> handleCartItemNotFound(CartItemNotFoundException e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleIllegalState(IllegalStateException e){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }
}
