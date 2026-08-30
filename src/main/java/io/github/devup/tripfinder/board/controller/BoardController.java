package io.github.devup.tripfinder.board.controller;

import io.github.devup.tripfinder.auth.repository.UsersRepository;
import io.github.devup.tripfinder.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/board")
public class BoardController {
    private final BoardService boardService;
    private final UsersRepository usersRepository;
}
