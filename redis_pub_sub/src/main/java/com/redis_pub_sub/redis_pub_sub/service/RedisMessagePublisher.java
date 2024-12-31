package com.redis_pub_sub.redis_pub_sub.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisMessagePublisher {

    @Autowired
    private StringRedisTemplate redisTemplate;

    // Publish a message to a Redis channel
    public void publishMessage(String topic, String message) {
        redisTemplate.convertAndSend(topic, message); // Publish to the topic
        System.out.println("Published message to " + topic + ": " + message);
    }
}
