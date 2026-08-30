package io.github.devup.tripfinder.board.controller;

import io.github.devup.tripfinder.auth.repository.UsersRepository;
import io.github.devup.tripfinder.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/board")
public class BoardController {
    private final BoardService boardService;
    private final UsersRepository usersRepository;

    @PostMapping("/upload")
    public ResponseEntity<List<String>>  uploadImages(@RequestParam("files") List<MultipartFile> files)throws IOException{
        List<String> urls = new ArrayList<>();
        String uploadDir = "uploads/board/"; //프로젝트 루트 기준 저장 폴더

        File dir = new File(uploadDir);
        if(!dir.exists()) dir.mkdirs();

        for(MultipartFile file : files){
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename(); //원래있는 파일이랑 안겹치게
            File dest = new File(uploadDir + fileName);
            file.transferTo(dest); //실제로 업로드된 파일 내용을 그 경로에 저장 (진짜 디스크에 쓰는 부분)
            urls.add("/uploads/board/" + fileName);  // 저장 성공한 파일의 접근 URL을 리스트에 추가
            // 이 URL이 나중에 게시글 작성 시 BoardCreateRequest.imgUrls에 담겨서 쓰임
        }
        return ResponseEntity.ok(urls); // 저장된 모든 이미지의 URL 목록을 응답으로 리턴
    }
}
