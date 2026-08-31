package io.github.devup.tripfinder.board.controller;

import io.github.devup.tripfinder.auth.repository.UsersRepository;
import io.github.devup.tripfinder.board.dto.request.BoardCreateRequest;
import io.github.devup.tripfinder.board.dto.request.BoardUpdateRequest;
import io.github.devup.tripfinder.board.dto.request.CommentCreateRequest;
import io.github.devup.tripfinder.board.dto.request.CommentUpdateRequest;
import io.github.devup.tripfinder.board.entity.Board;
import io.github.devup.tripfinder.board.entity.BoardComment;
import io.github.devup.tripfinder.board.repository.BoardRepository;
import io.github.devup.tripfinder.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/board")
public class BoardController {
    private final BoardService boardService;


    private Long getCurrentUserId(Authentication authentication) {
        if(authentication == null){
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }
        return (Long)  authentication.getPrincipal();
    }

    @PostMapping
    public ResponseEntity<?> createBoard(@RequestBody BoardCreateRequest request,Authentication authentication) {
        Board board = boardService.createBoard(getCurrentUserId(authentication),request);
        return ResponseEntity.ok(board.getId());
    }


    @GetMapping
    public ResponseEntity<List<Board>> getBoardList(@RequestParam(required = false) String category){
        return ResponseEntity.ok(boardService.getBoardList(category));
    }

    @GetMapping("/{boardId}")
    public ResponseEntity<Board> getBoard(@PathVariable Long boardId){
        return ResponseEntity.ok(boardService.getBoard(boardId));
    }

    @PutMapping("/{board}")
    public ResponseEntity<?> updateBoard(@PathVariable Long boardId ,
                                         @RequestBody BoardUpdateRequest request,
                                         Authentication authentication){
        Long requesterId = getCurrentUserId(authentication);
        boardService.updateBoard(boardId,requesterId,request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{boardId}")
    public ResponseEntity<?> deleteBoard(@PathVariable Long boardId,Authentication authentication){
        Long requesterId = getCurrentUserId(authentication);
        boardService.deleteBoard(boardId,requesterId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{boardId}/like")
    public ResponseEntity<?> likeBoard(@PathVariable Long boardId,Authentication authentication){
        Long requesterId = getCurrentUserId(authentication);
        boardService.toggleLike(boardId,requesterId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{boardId}/like")
    public ResponseEntity<?> getLikeInfo(@PathVariable Long boardId,Authentication authentication){
        Long requesterId = getCurrentUserId(authentication);
        long count = boardService.getLikeCount(boardId);
        boolean liked = boardService.isLiked(boardId,requesterId);
        return ResponseEntity.ok(Map.of("count",count,"liked",liked));
    }

    @PostMapping("/{boardId}/comments")
    public ResponseEntity<?> createComment(@PathVariable Long boardId,
                                           @RequestBody CommentCreateRequest reqeust,
                                           Authentication authentication){
        Long writerId = getCurrentUserId(authentication);
        BoardComment comment = boardService.createComment(boardId,writerId,reqeust);
        return ResponseEntity.ok(comment.getId());
    }

    @GetMapping("/{boardId}/comments")
    public ResponseEntity<List<BoardComment>> getComments(@PathVariable Long boardId){
        return ResponseEntity.ok(boardService.getComment(boardId));
    }

    @GetMapping("/comments/{commentId}")
    public ResponseEntity<?> updateComment(@PathVariable Long commentId,
                                           @RequestBody CommentUpdateRequest request,
                                           Authentication authentication){
        Long requesterId = getCurrentUserId(authentication);
        boardService.updateComment(commentId,requesterId,request.getComments());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId,Authentication authentication){
        Long requesterId = getCurrentUserId(authentication);
        boardService.deleteComment(commentId,requesterId);
        return ResponseEntity.ok().build();
    }



    @PostMapping("/upload")
    public ResponseEntity<List<String>>  uploadImages(@RequestParam("files") List<MultipartFile> files)throws IOException{
        List<String> urls = boardService.uploadImages(files);
        return ResponseEntity.ok(urls); // 저장된 모든 이미지의 URL 목록을 응답으로 리턴
    }
}
