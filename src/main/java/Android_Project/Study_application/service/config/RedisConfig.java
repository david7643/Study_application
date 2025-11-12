package Android_Project.Study_application.service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    // 1. Redis와 연결을 위한 ConnectionFactory 빈 생성
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(host, port);
    }

    // 2. Redis 데이터를 다루기 위한 RedisTemplate 빈 생성
    //    이메일 인증번호는 <String, String> 타입으로 저장할 것이므로
    //    RedisTemplate<String, String>으로 설정합니다.
    @Bean
    public RedisTemplate<String, String> redisTemplate() {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();

        // 방금 만든 ConnectionFactory를 템플릿에 설정
        redisTemplate.setConnectionFactory(redisConnectionFactory());

        // Key-Value 데이터를 문자열(String)로 직렬화(Serialize)하도록 설정
        // (이걸 안 하면 Redis에 알아볼 수 없는 문자로 저장됨)
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());

        return redisTemplate;
    }
}
