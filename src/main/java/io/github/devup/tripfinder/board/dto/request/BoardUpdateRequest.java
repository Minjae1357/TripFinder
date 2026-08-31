package io.github.devup.tripfinder.board.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor // 여기는 업데이트할떄 사용하는 dto
public class BoardUpdateRequest {
    private String title;
    private String contents;
}
