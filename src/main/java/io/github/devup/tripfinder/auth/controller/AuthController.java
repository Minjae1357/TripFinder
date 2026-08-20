package io.github.devup.tripfinder.auth.controller;

import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@CrossOrigin(origins = "http://localhost:5173")
@NoArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

}
