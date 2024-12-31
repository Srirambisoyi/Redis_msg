package com.redis_pub_sub.redis_pub_sub.controller;

import com.redis_pub_sub.redis_pub_sub.service.RedisMessagePublisher;
import com.redis_pub_sub.redis_pub_sub.service.RedisSubscriber;
import redis.clients.jedis.Jedis;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/redis")
public class RedisMessageController {
    private static final String TOPICS_KEY = "topics"; // Redis key to store all created topics


  
    @Autowired
    private RedisSubscriber redisSubscriber;
    @Autowired
    private Jedis jedis;

    // API to publish message to a channel/topic
    @PostMapping("/publish")
    public String publishMessage(@RequestParam String topic, @RequestParam String message) {
    	// Check if topic exists, if not, create it (publish first message)
        if (!isTopicPresent(topic)) {
            createTopic(topic, message);
        } else {
            // Overwrite the message if the topic already exists (just publish again)
            jedis.publish(topic, message);
        }
        return "Message published to " + topic;
    }

    // API to create a topic (topic creation is implicit by using it for publishing)
    @PostMapping("/create-topic")
    public String createTopic(@RequestParam String topic) {
        jedis.publish(topic, "good morning");
        return "Topic created: " + topic;
    }

    // API to delete a topic (topics are just channels in Redis, no direct deletion method)
    @DeleteMapping("/delete-topic")
    public String deleteTopic(@RequestParam String topic) {
        return "Topic deleted: " + topic;
    }

    // API to update a topic (changing a topic is like renaming, which is not directly supported in Redis)
    @PutMapping("/update-topic")
    public String updateTopic(@RequestParam String oldTopic, @RequestParam String newTopic) {
        return "Topic updated from " + oldTopic + " to " + newTopic;
    }

    // API to subscribe to a topic and wait for a message
    @PostMapping("/subscribe-topic")
    public String subscribeToTopic(@RequestParam String topic) {
        // Start subscribing to the topic
        redisSubscriber.subscribeToTopic(topic);

        // Wait for a message (this is blocking)
        try {
            String message = redisSubscriber.getReceivedMessage();  // This will block until a message is received
            return "Received message from topic '" + topic + "': " + message;
        } catch (Exception e) {
            return "No message received within the timeout period.";
        }
    }
    private boolean isTopicPresent(String topic) {
        // Use Redis' key checking mechanism. In this case, we'll use a 'SET' to track if the topic was created.
        return jedis.exists(topic); // Check if a key exists for the topic
    }

    // Create topic by publishing the message
    private void createTopic(String topic, String message) {
        System.out.println("Checking Redis connection...");  // Debugging output
        
        // Try a simple Redis command to verify the connection
        String pingResponse = jedis.ping();
        System.out.println("Redis ping response: " + pingResponse);  // Should return "PONG"
        
        jedis.sadd(TOPICS_KEY, topic);
        System.out.println("Added topic: " + topic);  // Debugging output
        
        jedis.set(topic, message);  // Store the first message for the topic
        jedis.publish(topic, message);  // Publish the first message

    }
    @GetMapping("/get-topics")
    public Set<String> getAllTopics() {
        Set<String> topics = new HashSet<>();  // Default to an empty set

        try {
            // Check if Redis connection is available
            String pingResponse = jedis.ping();
            System.out.println("Redis ping response: " + pingResponse);  // Should return "PONG"

            if ("PONG".equals(pingResponse)) {
                // Fetch the list of topics from the Redis Set
                topics = jedis.smembers(TOPICS_KEY);  // Get topics from Redis set
                System.out.println("Fetched topics from Redis: " + topics);  // Debugging output
            } else {
                System.err.println("Redis server not responding as expected.");
            }
        } catch (Exception e) {
            System.err.println("Error retrieving topics from Redis: " + e.getMessage());
            e.printStackTrace();  // Log stack trace for debugging
        }

        return topics;  // Return the set of topics
    }


  
}
