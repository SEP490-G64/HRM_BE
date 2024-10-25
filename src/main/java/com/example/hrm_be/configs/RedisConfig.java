package com.example.hrm_be.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.SessionIdGenerator;
import org.springframework.session.data.redis.RedisIndexedSessionRepository;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@Configuration
@EnableRedisHttpSession
public class RedisConfig implements BeanClassLoaderAware {
  private ClassLoader loader;

  @Bean
  public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
    return new GenericJackson2JsonRedisSerializer(objectMapper());
  }

  private ObjectMapper objectMapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModules(SecurityJackson2Modules.getModules(this.loader));
    return mapper;
  }

  @Override
  public void setBeanClassLoader(ClassLoader classLoader) {
    this.loader = classLoader;
  }

  // Create and inject the RedisConnectionFactory as a bean
  @Bean
  public RedisConnectionFactory redisConnectionFactory() {
    RedisStandaloneConfiguration config = new RedisStandaloneConfiguration("localhost", 6379);
    return new LettuceConnectionFactory(config); // Properly initialize LettuceConnectionFactory
  }

  // RedisOperations bean (used for session management)
  @Bean
  public RedisOperations<String, Object> sessionRedisOperations(
      RedisConnectionFactory redisConnectionFactory) {
    RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
    redisTemplate.setConnectionFactory(
        redisConnectionFactory); // Ensure RedisConnectionFactory is injected here
    redisTemplate.setKeySerializer(new StringRedisSerializer());
    redisTemplate.setHashKeySerializer(new StringRedisSerializer());
    return redisTemplate;
  }

  // Session repository bean (FindByIndexNameSessionRepository)
  @Bean
  public FindByIndexNameSessionRepository redisSessionRepository(
      RedisOperations<String, Object> sessionRedisOperations) {
    return new RedisIndexedSessionRepository(
        sessionRedisOperations); // Inject RedisOperations correctly
  }

  // Session ID generator bean
  @Bean
  public SessionIdGenerator sessionIdGenerator() {
    return new MySessionIdGenerator();
  }

  // Custom SessionIdGenerator implementation
  static class MySessionIdGenerator implements SessionIdGenerator {
    @Override
    public String generate() {
      return UUID.randomUUID().toString();
    }
  }

  @Bean
  public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(connectionFactory);
    template.setKeySerializer(new StringRedisSerializer());
    template.setValueSerializer(new StringRedisSerializer());
    return template;
  }
}
