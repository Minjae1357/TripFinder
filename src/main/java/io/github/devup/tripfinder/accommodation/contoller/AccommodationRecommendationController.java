package io.github.devup.tripfinder.accommodation.contoller;


import io.github.devup.tripfinder.accommodation.dto.response.AccommodationRecommendationResponse;
import io.github.devup.tripfinder.accommodation.service.AccommodationRecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/accommodations/recommend")
@RequiredArgsConstructor
public class AccommodationRecommendationController {
    private final AccommodationRecommendationService recommendationService;

    @GetMapping
    public List<AccommodationRecommendationResponse> recommend(@RequestParam String region){
        return recommendationService.recommendByRegion(region);
    }
}
