package com.project.binar.okariru.config;

import com.project.binar.okariru.service.appConfig.AppConfigProperties;
import io.lettuce.core.resource.DefaultClientResources;
import lombok.RequiredArgsConstructor;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.CacheKeyPrefix;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;

import java.time.Duration;
import java.util.Map;

@Configuration
@EnableCaching
@RequiredArgsConstructor
public class RedisConfig {

    private final AppConfigProperties appConfigProp;

    @Value("${app.redis.key-prefix}")
    private String redisKeyPrefix;

    private String normalizePrefix(String prefix) {
        if (prefix == null) return "";
        String p = prefix.trim();
        if (p.isEmpty()) return "";
        return p.endsWith(":") ? p : (p + ":");
    }

    private static final Duration DEFAULT_TTL = Duration.ofMinutes(10);

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(appConfigProp.getRedis().getHost());
        config.setPort(appConfigProp.getRedis().getPort());
        config.setUsername(appConfigProp.getRedis().getUsername());
        config.setPassword(RedisPassword.of(appConfigProp.getRedis().getPassword()));

        LettucePoolingClientConfiguration.LettucePoolingClientConfigurationBuilder builder =
                LettucePoolingClientConfiguration.builder();

        builder
                .clientResources(DefaultClientResources.create())
                .commandTimeout(appConfigProp.getRedis().getTimeout())
                .poolConfig(new GenericObjectPoolConfig<>() {{
                    setMaxTotal(appConfigProp.getRedis().getLettucePoolMaxActive());
                    setMaxIdle(appConfigProp.getRedis().getLettucePoolMaxIdle());
                    setMinIdle(appConfigProp.getRedis().getLettucePoolMinIdle());
                    setMaxWait(appConfigProp.getRedis().getLettucePoolMaxWait());
                }});

        LettuceClientConfiguration clientConfiguration = builder.build();

        return new LettuceConnectionFactory(config, clientConfiguration);
    }

    @Bean
    public GenericJacksonJsonRedisSerializer cacheValueSerializer() {
        PolymorphicTypeValidator typeValidator =
                BasicPolymorphicTypeValidator.builder()
                        .allowIfSubType("com.project.binar.okariru")
                        .allowIfSubType("java.util")
                        .build();

        return GenericJacksonJsonRedisSerializer.builder()
                .enableDefaultTyping(typeValidator)
                .typePropertyName("@class")
                .build();
    }


    @Bean
    public RedisCacheManager cacheManager(
            RedisConnectionFactory connectionFactory,
            GenericJacksonJsonRedisSerializer valueSerializer) {

        RedisCacheConfiguration configuration =
                RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.ofMinutes(10))
                        .disableCachingNullValues()
                        .computePrefixWith(CacheKeyPrefix.prefixed(normalizePrefix(redisKeyPrefix)))
                        .serializeKeysWith(
                                RedisSerializationContext.SerializationPair.fromSerializer(
                                        new StringRedisSerializer()
                                )
                        )
                        .serializeValuesWith(
                                RedisSerializationContext.SerializationPair.fromSerializer(
                                        valueSerializer
                                )
                        );
        Map<String, RedisCacheConfiguration> perCache = Map.of(
                "menu:my",  configuration.entryTtl(Duration.ofMinutes(30)),
                "pinjaman", configuration.entryTtl(Duration.ofHours(1))
        );
        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(configuration)
                .withInitialCacheConfigurations(perCache)
                .build();
    }


    @Bean
    public RedisTemplate<String, Object> redisTemplate(
            RedisConnectionFactory connectionFactory
    ) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();

        RedisSerializer<String> stringSerializer =
                RedisSerializer.string();

        RedisSerializer<Object> jsonSerializer =
                RedisSerializer.json();

        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);
        template.setDefaultSerializer(jsonSerializer);
        template.afterPropertiesSet();

        return template;
    }

}
