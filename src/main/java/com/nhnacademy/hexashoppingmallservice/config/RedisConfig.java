package com.nhnacademy.hexashoppingmallservice.config;


import com.nhnacademy.hexashoppingmallservice.credentials.RedisCredentials;

import com.nhnacademy.hexashoppingmallservice.service.credentials.SecureKeyManagerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;


import java.util.Map;

@Configuration
@Slf4j
public class RedisConfig {

    private final SecureKeyManagerService secureKeyManagerService;

    private final String host = "10.116.64.14";
    private final int port = 6379;
    private final String password = "*N2vya7H@muDTwdNMR!";
    private final int database = 255;

    @Value("${redis.cart.keyId}")
    private String keyId;

    public RedisConfig(SecureKeyManagerService secureKeyManagerService) {
        this.secureKeyManagerService = secureKeyManagerService;
    }


    @Bean
    public RedisConnectionFactory redisConnectionFactory(){

//        RedisCredentials databaseCredentials =new RedisCredentials(secureKeyManagerService.fetchSecretFromKeyManager(keyId));

//        Map<String, String> credentialsMap = databaseCredentials.getCredentialsMap();
//        log.info("Fetched Redis credentials: {}", credentialsMap);

//        String host = credentialsMap.get("host");
//        int port = Integer.parseInt(credentialsMap.get("port"));
//        String password = credentialsMap.get("password");
//        int database = Integer.parseInt(credentialsMap.get("database"));


        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
        config.setPassword(password);
        config.setDatabase(database);
        return new JedisConnectionFactory(config);
    }


        @Bean
        public RedisTemplate<String, String> redisTemplate() {
            RedisTemplate<String, String> template = new RedisTemplate<>();
            template.setConnectionFactory(redisConnectionFactory());
            template.setKeySerializer(new StringRedisSerializer());
            template.setValueSerializer(new StringRedisSerializer());
            return template;
        }
}
