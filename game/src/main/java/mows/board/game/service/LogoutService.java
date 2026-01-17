package mows.board.game.service;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LogoutService {
    private final RedisTemplate<String, Object> redisTemplate;
    
    public void logout(String token) {
        // "logout:토근값"
        redisTemplate.opsForValue().set("logout:" + token, "blacklisted", 24, TimeUnit.HOURS);
    }
    
    public boolean isBlackListed(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey("logout:" + token));
    }
}
