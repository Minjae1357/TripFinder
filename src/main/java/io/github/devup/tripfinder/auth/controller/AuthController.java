package io.github.devup.tripfinder.auth.controller;

import io.github.devup.tripfinder.auth.dto.request.SignupRequest;
import io.github.devup.tripfinder.auth.entity.Users;
import io.github.devup.tripfinder.auth.service.AuthService;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public Users signup(@RequestBody SignupRequest signupRequest) {
        return authService.signup(signupRequest);
    }
}
