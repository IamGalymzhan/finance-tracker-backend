package org.galymzhan.financetrackerbackend.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "jwt")
@Getter
@Setter
public class JwtProperties {

    private String signingKey;

    private Long expirationMs = 86400000L;

    private Long refreshExpirationMs = 604800000L;

    public long getExpirationSeconds() {
        return expirationMs / 1000;
    }

    public long getRefreshExpirationSeconds() {
        return refreshExpirationMs / 1000;
    }
}