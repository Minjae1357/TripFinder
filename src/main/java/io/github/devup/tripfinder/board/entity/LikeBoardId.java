package io.github.devup.tripfinder.board.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

//Like보드의 복합키임 전용 클래스
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LikeBoardId {
    private Long user; //LikeBoard의 @id 필드명(user)과 이름이 같아야함
    private Long board; // 위랑똑같은 (board)
}
