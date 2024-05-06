package com.codeanalyser.shop.auth.supabase;

import io.supabase.data.dto.UserDto;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

public class SupabaseUserDetails implements UserDetails {

    private final UserDto userDto;

    public SupabaseUserDetails(UserDto userDto) {
        this.userDto = userDto;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Map the role to a Spring Security "GrantedAuthority"
        return Collections.singletonList(new SimpleGrantedAuthority(Objects.equals(userDto.getRole(), "admin") ? "ROLE_ADMIN" : "ROLE_USER"));
    }

    @Override
    public String getPassword() {
        // Supabase might not send passwords depending on the setup; return null or a blank string
        return null;
    }

    @Override
    public String getUsername() {
        // Use email as the username for Spring Security purposes
        return userDto.getEmail();
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
