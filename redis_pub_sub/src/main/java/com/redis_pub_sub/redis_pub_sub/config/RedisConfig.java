package com.redis_pub_sub.redis_pub_sub.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.listener.PatternTopic;

import com.redis_pub_sub.redis_pub_sub.listener.RedisMessageSubscriber;

import redis.clients.jedis.Jedis;

@Configuration
public class RedisConfig {

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory redisConnectionFactory, RedisMessageSubscriber subscriber) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);

        // Create a listener adapter for the subscriber
        MessageListenerAdapter listenerAdapter = new MessageListenerAdapter(subscriber, "onMessage");
        
        // Add the message listener with a specific topic (or a wildcard topic)
        container.addMessageListener(listenerAdapter, new PatternTopic("welcome"));

        return container;
    }
    @Bean
    public Jedis jedis() {
        // Set up Jedis client to connect to the Redis server
        return new Jedis("localhost", 6379); // You can customize the Redis host and port if needed
    }
}
