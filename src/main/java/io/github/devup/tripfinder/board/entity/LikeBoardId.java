package io.github.devup.tripfinder.board.entity;


import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

//Like보드의 복합키임 전용 클래스
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode //[필수] JPA 영속성 컨텍스트 동일성 비교용
public class LikeBoardId implements Serializable {
    private Long user; //LikeBoard의 @id 필드명(user)과 이름이 같아야함
    private Long board; // 위랑똑같은 (board)
}
