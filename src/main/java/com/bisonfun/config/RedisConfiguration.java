package com.bisonfun.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;

@Configuration
public class RedisConfiguration {
@Bean
public RedisTemplate<Long, String> redisTemplate(RedisConnectionFactory connectionFactory) {
    RedisTemplate<Long, String> template = new RedisTemplate<>();
    template.setConnectionFactory(connectionFactory);
    template.setValueSerializer(new GenericToStringSerializer<>(String.class));
    // Add some specific configuration here. Key serializers, etc.
    return template;
}
}
