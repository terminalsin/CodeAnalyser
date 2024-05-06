package com.codeanalyser.shop.auth.supabase;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class SupabaseConfig {
    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.api.key}")
    private String apiKey;

    @Value("${supabase.jwt}")
    private String jwt;

    public String getSupabaseUrl() {
        return supabaseUrl;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getJwt() {
        return jwt;
    }
}
