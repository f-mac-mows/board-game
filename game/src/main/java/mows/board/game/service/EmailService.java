package mows.board.game.service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String AUTH_PREFIX = "email_auth:";

    public void sendVerificationEmail(String email) {
        String code = String.format("%06d", new Random().nextInt(1000000));

        // Redis에 5분간 인증코드 저장
        redisTemplate.opsForValue().set(AUTH_PREFIX + email, code, 5, TimeUnit.MINUTES);
        
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject(("[BoardGame] 화원가입 인증 번호입니다."));
        message.setText("인증 번호: " + code);
        mailSender.send(message);
    }

    public boolean verifyCode(String email, String code) {
        String savedCode = (String) redisTemplate.opsForValue().get(AUTH_PREFIX + email);
        return code.equals(savedCode);
    }
}
