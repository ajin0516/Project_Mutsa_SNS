package com.finalproject_sns.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class EncrypterConfig{

    @Bean
    public BCryptPasswordEncoder encoderPed() {
        return new BCryptPasswordEncoder();  // password 인코딩할 떄 사용
    }
}
