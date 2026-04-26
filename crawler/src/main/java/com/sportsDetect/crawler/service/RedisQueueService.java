package com.sportsDetect.crawler.service;

/*import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisQueueService {
    private static final String QUEUE_NAME = "crawler:links";

    @Autowired
    private StringRedisTemplate redisTemplate;

    public void push(String data) {
        try {
            redisTemplate.opsForList().rightPush(QUEUE_NAME, data);
        } catch (Exception e){
        }
    }

    public String pop() {
        return redisTemplate.opsForList().leftPop(QUEUE_NAME);
    }

    public Long size() {
         return redisTemplate.opsForList().size(QUEUE_NAME);
    }

    public String blockingPop() {
        try {
            return redisTemplate.opsForList()
                    .leftPop(QUEUE_NAME, 5, TimeUnit.SECONDS);
        } catch (Exception e){
            return null;
        }
    }
}*/
