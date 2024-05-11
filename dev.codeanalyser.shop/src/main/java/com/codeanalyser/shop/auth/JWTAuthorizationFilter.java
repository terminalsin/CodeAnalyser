package com.codeanalyser.shop.auth;

import com.codeanalyser.shop.auth.supabase.SupabaseUserDetails2;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;

public class JWTAuthorizationFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;

    public JWTAuthorizationFilter(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            try {
                String token = header.substring(7);
                Algorithm algorithm = Algorithm.HMAC256("bHqf1AMjdbSSKn3MlGJW//yHf+RCW4BQkNcrQ0ifZ/EBt+7ixKFvjPtYgU6B3ZkYKp3H0hlVTezWfgr5cJTN3A=="); // Replace "secret" with your secret key
                JWTVerifier verifier = JWT.require(algorithm).build();
                DecodedJWT jwt = verifier.verify(token);
                String username = jwt.getSubject();

                System.out.println("Username: " + username);

                if (username != null) {
                    UserDetails userDetails = new SupabaseUserDetails2(username); // Replace with your own UserDetails implementation (e.g. SupabaseUserDetails
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null,
                            userDetails.getAuthorities()
                    );
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    Principal principal = SecurityContextHolder.getContext().getAuthentication();
                    System.out.println("xxx Principal: " + principal + " " + principal.getName());
                }
            } catch (JWTVerificationException exception) {
                SecurityContextHolder.clearContext(); // Clear context to ensure it is not used accidentally
            }
        }

        filterChain.doFilter(request, response);
    }
}
