package io.github.devup.tripfinder.cart.dto.response;

import java.math.BigDecimal;
import java.util.List;

// 장바구니 전체 항목들의 응답 - 목록, 총 합계 금액
public record CartResponse(
        Long cartId,
        List<CartItemResponse> items,
        BigDecimal totalAmount // items의 총 합 금액
) {
}
