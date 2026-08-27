package io.github.devup.tripfinder.accommodationreview.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Component
@Slf4j
// 파일 저장
public class FileStorageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${file.base-url:http://localhost:8080}")
    private String baseUrl;

    // MultipartFile을 서버 디스크에 저장, 브라우저에 접근 가능 URL반환
    public String store(MultipartFile file) {
        try{
            Path dirPath = Path.of(uploadDir);
            if(!Files.exists(dirPath)){     // 폴더가 없으면 자동으로 생성
                Files.createDirectories(dirPath);
            }
            // 여러 유저가 같은 이름의 파일을 저장할 경우를 대비하여 UUID를 붙여서 저장
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String storedFilename = UUID.randomUUID() + extension;

            Path targetPath = dirPath.resolve(storedFilename);
            file.transferTo(targetPath); // 실제 파일 저장

            return baseUrl + "/uploads/review/" + storedFilename; // WebConfig 정적 리소스 경로와 매칭
        } catch (IOException e) {
            throw new RuntimeException("파일 저장에 실패했습니다.", e);
        }
    }
}
