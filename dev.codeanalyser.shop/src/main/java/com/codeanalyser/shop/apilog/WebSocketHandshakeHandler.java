package com.codeanalyser.shop.apilog;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.codeanalyser.shop.auth.supabase.SupabaseUserDetails2;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.Map;

@Component
public class WebSocketHandshakeHandler extends DefaultHandshakeHandler {
    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();
        String header = extractTokenFromCookie(servletRequest); // Extract token from cookie (you can also extract it from header or query parameter

        //System.out.println("hzehe Header: " + header);

        if (header != null) {
            try {
                String token = header.substring(7);
                Algorithm algorithm = Algorithm.HMAC256("bHqf1AMjdbSSKn3MlGJW//yHf+RCW4BQkNcrQ0ifZ/EBt+7ixKFvjPtYgU6B3ZkYKp3H0hlVTezWfgr5cJTN3A=="); // Replace "secret" with your secret key
                JWTVerifier verifier = JWT.require(algorithm).build();
                DecodedJWT jwt = verifier.verify(token);
                String username = jwt.getSubject();

                //System.out.println("Username: " + username);

                if (username != null) {
                    UserDetails userDetails = new SupabaseUserDetails2(username); // Replace with your own UserDetails implementation (e.g. SupabaseUserDetails
                    return new UsernamePasswordAuthenticationToken(
                            userDetails, null,
                            userDetails.getAuthorities()
                    );
                }
            } catch (JWTVerificationException exception) {
                SecurityContextHolder.clearContext(); // Clear context to ensure it is not used accidentally
                exception.printStackTrace();
            }
        }

        return super.determineUser(request, wsHandler, attributes);
    }

    public String extractTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().startsWith("sb-") && cookie.getName().endsWith("-auth-token")) {
                    String tokenJson = java.net.URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8);
                    JsonObject jsonObject = new JsonParser().parse(tokenJson).getAsJsonObject();
                    return jsonObject.get("access_token").getAsString();
                }
            }
        }
        return null;
    }
}
