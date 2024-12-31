package com.redis_pub_sub.redis_pub_sub.listener;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
public class RedisMessageSubscriber implements MessageListener {

	 @Override
	    public void onMessage(Message message, byte[] pattern) {
	        // Extract the channel and message body
	        String channel = new String(message.getChannel());
	        String messageBody = new String(message.getBody());
	        
	        // Log or process the received message
	        System.out.println("Received message from channel " + channel + ": " + messageBody);
	    }
    
    
    
    
 
}
