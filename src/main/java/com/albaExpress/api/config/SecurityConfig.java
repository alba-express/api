package com.albaExpress.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()  // 개발 중에는 CSRF 보호를 비활성화할 수 있습니다. 필요에 따라 설정하십시오.
                .cors().and()  // CORS 설정 활성화
                .authorizeRequests()
                .antMatchers("/api/auth/**").permitAll()  // 인증 관련 경로는 모든 사용자에게 허용합니다.
                .anyRequest().authenticated();

        return http.build();
    }
}
