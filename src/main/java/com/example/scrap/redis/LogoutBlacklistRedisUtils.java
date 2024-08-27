package com.example.scrap.redis;

import com.example.scrap.base.data.DefaultData;
import com.example.scrap.entity.Member;
import com.example.scrap.jwt.ITokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class LogoutBlacklistRedisUtils implements ILogoutBlacklistRedisUtils {

    @Value("${jwt.expire.access_hour}")
    private int expireHourOfAccessToken;

    private final RedisTemplate<String, String> redisTemplate;
    private final ITokenProvider tokenProvider;

    @Override
    public void addLogoutToken(String token, Member member) {
        String key = createKey(token);
        String value = createValue(member);

        redisTemplate.opsForValue()
                .set(key, value, expireHourOfAccessToken, TimeUnit.HOURS);
    }

    @Override
    public boolean existToken(String token) {
        String key = createKey(token);

        if(redisTemplate.opsForValue().get(key) == null){
            return false;
        }
        else {
            return true;
        }
    }

    private String createKey(String token) {
        token = tokenProvider.removeTokenPrefix(token);
        return DefaultData.REDIS_KEY_PREFIX + token;
    }

    private String createValue(Member member){
        return DefaultData.REDIS_VALUE_PREFIX + member.getId();
    }
}
