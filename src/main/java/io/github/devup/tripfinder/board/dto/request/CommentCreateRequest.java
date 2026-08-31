package io.github.devup.tripfinder.board.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentCreateRequest {
    private String contents;
    private Long parentId; // 일반댓글이면 null 보내고 , 대댓글이면 부모 댓글 id 보내기
}
