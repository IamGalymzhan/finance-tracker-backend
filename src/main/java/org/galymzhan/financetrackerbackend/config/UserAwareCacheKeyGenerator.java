package org.galymzhan.financetrackerbackend.config;

import org.galymzhan.financetrackerbackend.service.AuthenticationService;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component("userAwareKeyGenerator")
public class UserAwareCacheKeyGenerator implements KeyGenerator {

    private final AuthenticationService authenticationService;

    public UserAwareCacheKeyGenerator(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Override
    @NonNull
    public Object generate(@NonNull Object target, @NonNull Method method, Object... params) {
        Long userId = authenticationService.getCurrentUser().getId();
        StringBuilder keyBuilder = new StringBuilder();
        keyBuilder.append(userId);

        for (Object param : params) {
            keyBuilder.append("-").append(param);
        }

        return keyBuilder.toString();
    }
}