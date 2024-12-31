package com.redis_pub_sub.redis_pub_sub.service;

import com.redis_pub_sub.redis_pub_sub.listener.RedisMessageSubscriber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TopicService {

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    @Autowired
    private RedisMessageSubscriber redisMessageSubscriber;
    @Autowired

    private RedisMessageListenerContainer container;

    // Constructor injection ensures dependencies are properly initialized
    public TopicService(RedisConnectionFactory redisConnectionFactory, RedisMessageSubscriber redisMessageSubscriber) {
        this.redisConnectionFactory = redisConnectionFactory;
        this.redisMessageSubscriber = redisMessageSubscriber;
        this.container = new RedisMessageListenerContainer();
        this.container.setConnectionFactory(redisConnectionFactory);
    }

    /**
     * Subscribe to the provided list of topics
     */
    public void subscribeToTopics(List<String> topics) {
        topics.forEach(topic -> {
            // Adding message listener to the container for each topic
            container.addMessageListener(new MessageListenerAdapter(redisMessageSubscriber), new PatternTopic(topic));
            System.out.println("Subscribed to topic: " + topic);
        });

        container.start();
    }

    /**
     * Publish a message to a specific topic
     */
    public void publishMessageToTopic(String topic, String message) {
        container.getConnectionFactory().getConnection().publish(topic.getBytes(), message.getBytes());
        System.out.println("Published message: " + message + " to topic: " + topic);
    }

    /**
     * Unsubscribe from all topics (or you could implement unsubscribing from specific topics)
     */
    public void unsubscribeFromTopics(List<String> topics) {
        topics.forEach(topic -> {
            // Remove listeners for the topics
            container.removeMessageListener(new MessageListenerAdapter(redisMessageSubscriber), new PatternTopic(topic));
            System.out.println("Unsubscribed from topic: " + topic);
        });
    }

    /**
     * Update topic subscription (e.g., add new topic or remove old one)
     */
    public void updateTopicSubscription(List<String> oldTopics, List<String> newTopics) {
        unsubscribeFromTopics(oldTopics);  // Unsubscribe from old topics
        subscribeToTopics(newTopics);      // Subscribe to new topics
    }
}
