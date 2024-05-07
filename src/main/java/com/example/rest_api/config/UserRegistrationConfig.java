package com.example.rest_api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UserRegistrationConfig {

    @Value("${user.registration.min-age}")
    private int minAge;

    public int getMinAge() {
        return minAge;
    }
}
