package io.github.devup.tripfinder.board.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor //JSON → 객체로 변환(역직렬화)할 때 스프링이 기본생성자로 빈 객체 만들고 필드를 채워넣는 방식이라 필수
public class BoardCreateRequest {
    private String category; //프론트에서 notice or review를 문자열로보냄
    private String title;
    private String contents;
    private List<String> imgUrls;
}
