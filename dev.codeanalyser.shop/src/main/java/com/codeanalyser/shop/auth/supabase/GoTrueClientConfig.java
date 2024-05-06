package com.codeanalyser.shop.auth.supabase;

import io.supabase.GoTrueClient;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class GoTrueClientConfig {

    @Value("${gotrue.api.url}")
    private String apiUrl;

    @Value("${gotrue.api.headers.authorization}")
    private String authorizationHeader;

    @Value("${gotrue.api.headers.content-type}")
    private String contentTypeHeader;

    @Value("${gotrue.api.jwt.secret}")
    private String jwtSecret;

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    @SneakyThrows
    public GoTrueClient goTrueClient() {
        System.setProperty("gotrue.url", apiUrl);
        System.setProperty("gotrue.headers", "apikey=" + authorizationHeader + ',' + "Content-Type="+ contentTypeHeader);
        System.setProperty("gotrue.jwt.secret", jwtSecret);
        return GoTrueClient.i();
    }
}
