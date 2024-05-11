package com.codeanalyser.shop.apilog;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.codeanalyser.shop.auth.supabase.SupabaseUserDetails2;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class WebSocketChannelInterceptor implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.wrap(message);
        String header = accessor.getFirstNativeHeader("Authorization");

        System.out.println("hzehe efzeffez Header: " + header);

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
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    // Update the message with the authenticated user
                    accessor.setUser(authentication);
                    return MessageBuilder.createMessage(message.getPayload(), accessor.getMessageHeaders());
                }
            } catch (JWTVerificationException exception) {
                SecurityContextHolder.clearContext(); // Clear context to ensure it is not used accidentally
            }
        }

        return message;
    }
}