package com.project.binar.okariru.service.appConfig;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@Data
@ConfigurationProperties(prefix = "app")
public class AppConfigProperties {
    private Redis redis;

    @Data
    public static class Redis {
        private String keyPrefix;
        private String host;
        private Integer port;
        private Duration timeout;
        private String username;
        private String password;
        private Integer lettucePoolMaxActive;
        private Duration lettucePoolMaxWait;
        private Integer lettucePoolMaxIdle;
        private Integer lettucePoolMinIdle;

    }
}
