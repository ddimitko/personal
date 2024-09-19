package com.ddimitko.personal.redisTestOnly;

import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisCleanupService implements ApplicationListener<ApplicationReadyEvent> {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final Logger logger = LoggerFactory.getLogger(RedisCleanupService.class);

    public RedisCleanupService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        logger.info("Clearing Redis data...");
        redisTemplate.getConnectionFactory().getConnection().flushDb();
    }
}
