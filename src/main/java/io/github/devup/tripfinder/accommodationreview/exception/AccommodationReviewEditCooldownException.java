package io.github.devup.tripfinder.accommodationreview.exception;

// 리뷰 수정, 등록 10초 이내에 수정시 던지는 예외처리
public class AccommodationReviewEditCooldownException extends RuntimeException{
    public AccommodationReviewEditCooldownException(String message){
        super(message);
    }
}
