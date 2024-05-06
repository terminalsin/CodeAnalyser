package com.codeanalyser.shop.auth.supabase;

import io.supabase.data.dto.UserDto;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

public class SupabaseUserDetails2 implements UserDetails {

    private final String username;

    public SupabaseUserDetails2(String userDto) {
        this.username = userDto;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Map the role to a Spring Security "GrantedAuthority"
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        // Supabase might not send passwords depending on the setup; return null or a blank string
        return null;
    }

    @Override
    public String getUsername() {
        // Use email as the username for Spring Security purposes
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        // Implement based on your application's logic, possibly using userDto fields
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // Implement based on your application's logic, possibly using userDto fields
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // Implement based on your application's logic, possibly using userDto fields
        return true;
    }

    @Override
    public boolean isEnabled() {
        // Consider using a field from userDto to determine if the account is enabled
        return true;
    }
}
