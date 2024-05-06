package com.codeanalyser.shop.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, @Autowired UserDetailsService userDetailsService) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disable CSRF as we're using a token-based authentication
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/use-agents/**").authenticated()  // Use requestMatchers for matching URLs
                        .anyRequest().permitAll())
                .addFilterBefore(jwtAuthorizationFilter(userDetailsService), UsernamePasswordAuthenticationFilter.class);  // Add custom JWT filter

        return http.build();
    }

    @Bean
    public JWTAuthorizationFilter jwtAuthorizationFilter(@Autowired UserDetailsService userDetailsService) {
        return new JWTAuthorizationFilter(userDetailsService);
    }
}
