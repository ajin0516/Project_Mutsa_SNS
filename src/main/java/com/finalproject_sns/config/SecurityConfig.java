package com.finalproject_sns.config;

import com.finalproject_sns.config.filter.JwtTokenFilter;
import com.finalproject_sns.service.UserService;
import com.finalproject_sns.utils.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserService userService;

    @Value("${jwt.token.secret}")
    private String secretKey;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .httpBasic().disable()
                .csrf().disable()
                .cors().and()
                .authorizeRequests()
                .antMatchers("/api/v1/users/join","/api/v1/users/login").permitAll()
                .antMatchers(HttpMethod.DELETE,"/api/**").authenticated() // delete는 인가자만 허용
                .antMatchers(HttpMethod.PUT,"/api/**").authenticated() // put는 인가자만 허용
                .antMatchers(HttpMethod.POST,"/api/v1/**").authenticated() // 모든 post요청 권한 막기
                .antMatchers("/api/v1/users/*/role/change").access("hasRole('ADMIN')")
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling()
                /*
                 401, 403 Exception 핸들링
                 */
//                .accessDeniedHandler(new CustomAccessDeniedHandler())
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                .and()
                .addFilterBefore(new JwtTokenFilter(userService, secretKey), UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
