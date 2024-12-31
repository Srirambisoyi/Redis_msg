package com.redis_pub_sub.redis_pub_sub.service;

import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
@Component
public class RedisSubscriber {

    private Jedis jedis;
    private String receivedMessage = null;


    public RedisSubscriber() {
        // Connect to Redis server (localhost:6379)
        this.jedis = new Jedis("localhost", 6379);
    }

    // Method to subscribe to a topic/channel
    public void subscribeToTopic(String topic) {
        new Thread(() -> {
            // JedisPubSub allows you to define how to handle incoming messages
            jedis.subscribe(new JedisPubSub() {
                @Override
                public void onMessage(String channel, String message) {
                    // This method is called whenever a message is published to the channel
                    System.out.println("Received message from topic " + channel + ": " + message);
                    receivedMessage=message;
                }

                @Override
                public void onSubscribe(String channel, int subscribedChannels) {
                    // Called when subscribing to the channel
                    System.out.println("Subscribed to channel: " + channel);
                }

                @Override
                public void onUnsubscribe(String channel, int subscribedChannels) {
                    // Called when unsubscribed from the channel
                    System.out.println("Unsubscribed from channel: " + channel);
                }
            }, topic);  // Subscribe to the given topic (channel)
        }).start();
    }
 // Method to get the latest received message
    public String getReceivedMessage() {
        return receivedMessage;
    }
}

