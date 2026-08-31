package io.github.devup.tripfinder.accommodation.contoller;

import io.github.devup.tripfinder.accommodation.dto.response.AccommodationDetailResponse;
import io.github.devup.tripfinder.accommodation.dto.response.AccommodationListResponse;
import io.github.devup.tripfinder.accommodation.exception.AccommodationNotFoundException;
import io.github.devup.tripfinder.accommodation.service.AccommodationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/accommodations")
@RequiredArgsConstructor

public class AccommodationController {
    private final AccommodationService accommodationService;

    // 숙소 상세 조회
    @GetMapping("/{accommodationId}")
    public AccommodationDetailResponse getDetail(@PathVariable Long accommodationId) {
        return accommodationService.getAccommodationDetail(accommodationId);
    }
    // 지도 마커 간략 조회
    @GetMapping
    public List<AccommodationListResponse> getList(){
        return accommodationService.getAccommodationList();
    }

    // 예외 처리
    @ExceptionHandler(AccommodationNotFoundException.class)
    public ResponseEntity<String> handleAccommodationNotFound(AccommodationNotFoundException e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
}

