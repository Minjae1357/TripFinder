package io.github.devup.tripfinder.accommodation.contoller;

import io.github.devup.tripfinder.accommodation.dto.response.AccommodationDetailResponse;
import io.github.devup.tripfinder.accommodation.service.AccommodationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accommodations")
@RequiredArgsConstructor

public class AccommodationController {
    private final AccommodationService accommodationService;

    @GetMapping("/{accommodationId}")
    public AccommodationDetailResponse getDetail(@PathVariable Long accommodationId) {
        return accommodationService.getAccommodationDetail(accommodationId);
    }
}

