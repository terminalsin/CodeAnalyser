package com.codeanalyser.shop.auth.supabase;

import io.supabase.GoTrueClient;
import io.supabase.data.dto.UserDto;
import io.supabase.exceptions.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class SupabaseUserDetailsService implements UserDetailsService {

    @Autowired
    private GoTrueClient goTrueClient;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Retrieve the user's JWT from the Supabase database
        UserDto jwt = null;
        try {
            jwt = goTrueClient.getUser(username);
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }

        // Create a new UserDetails object
        return new SupabaseUserDetails(jwt);
    }
}