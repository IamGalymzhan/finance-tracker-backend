package org.galymzhan.financetrackerbackend.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CustomCacheErrorHandler implements CacheErrorHandler {

    @Override
    public void handleCacheGetError(RuntimeException exception, Cache cache, @NonNull Object key) {
        log.warn("Cache GET error for cache '{}' with key '{}': {}. Falling back to database.",
                cache.getName(), key, exception.getMessage());
    }

    @Override
    public void handleCachePutError(RuntimeException exception, Cache cache, @NonNull Object key, Object value) {
        log.warn("Cache PUT error for cache '{}' with key '{}': {}. Data will not be cached.",
                cache.getName(), key, exception.getMessage());
    }

    @Override
    public void handleCacheEvictError(RuntimeException exception, Cache cache, @NonNull Object key) {
        log.warn("Cache EVICT error for cache '{}' with key '{}': {}. Cache entry may not be cleared.",
                cache.getName(), key, exception.getMessage());
    }

    @Override
    public void handleCacheClearError(RuntimeException exception, Cache cache) {
        log.warn("Cache CLEAR error for cache '{}': {}. Cache may not be fully cleared.",
                cache.getName(), exception.getMessage());
    }
}