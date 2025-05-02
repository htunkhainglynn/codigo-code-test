package com.codigo.code.test.service.impl;

import com.codigo.code.test.exception.ApplicationException;
import com.codigo.code.test.service.CacheService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
public class RedisService implements CacheService {
    private final RedisTemplate<String, String> redisTemplate;

    public RedisService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void set(String key, String value, int ttlInMin) {
        try {
            redisTemplate.opsForValue().set(key, value, Duration.ofMinutes(ttlInMin));
        } catch (Exception e) {
            throw new ApplicationException("Error setting value in Redis", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public String get(String key) {
        try {
            return redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            throw new ApplicationException("Error setting value in Redis", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public String checkSlot(String courseId) {
        String key = "course:" + courseId + ":available_slot";
        DefaultRedisScript<String> script = new DefaultRedisScript<>();
        script.setScriptText(
                """
                    local key = KEYS[1]
                    local slots = tonumber(redis.call("GET", key))
                                          
                    if slots and slots > 0 then
                        redis.call("DECR", key)
                        return "Booked"
                    else
                        return "Pending"
                    end
                                        
                """
        );
        script.setResultType(String.class);

        return redisTemplate.execute(script, List.of(key));
    }

}
