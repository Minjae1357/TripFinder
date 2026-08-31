package io.github.devup.tripfinder.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    private final StringRedisTemplate redisTemplate; //Redis 연동 템플릿 부분

    // 6자리 random 인증번호 생성하는 부분
    private String createCode(){
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }



    public void sendVerificationCode(String toEmail){ //검증 코드 전송

        // 인증번호 생성하는 부분
        String authCode = createCode();
        //redis에 key:이메일 , value:인증번호 5분간저장하는 부분
        redisTemplate.opsForValue().set(toEmail,authCode, Duration.ofMinutes(5));

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail); //수신자 이메일부분
        message.setSubject("[TripFinder] 회원가입 이메일 인증번호입니다."); //제목설정
        message.setText("인증번호" + authCode +"\n\n5분 이내에 입력해주세요."); //내용설정

        mailSender.send(message); //메일전송
    }

    public boolean verifyCode(String toEmail,String inputCode){
        // redis에 이메일(key)로 저장된 인증버호 가져오는 부분
        String saveCode = redisTemplate.opsForValue().get(toEmail);

        //만료거나 null일겨우 실패
        if(saveCode == null || !saveCode.equals(inputCode)){
            return false;
        }

        // 검증 성공시 redis에서 인증번호 삭제 후 트루반환함
        redisTemplate.delete(toEmail);
        // 이메일_VERIFIED 키로 인증 성공 표식을 redis에 5분간 저장하는곳
        redisTemplate.opsForValue().set(toEmail +"_VERIFIED","true",Duration.ofMinutes(5));
        return true;
    }

    public boolean isEmailVerified(String email){
        String isVerified = redisTemplate.opsForValue().get(email+"_VERIFIED");
        return "true".equals(isVerified);
    }
}
