package com.codeanalyser.shop.apilog;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.codeanalyser.shop.auth.supabase.SupabaseUserDetails2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;

import java.security.Principal;

@Configuration
@EnableWebSocketMessageBroker
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  @Autowired
  private WebSocketChannelInterceptor handshakeInterceptor;

  @Autowired
    private WebSocketHandshakeHandler handshakeHandler;

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint("/ws")
            .setAllowedOrigins("http://localhost:3000");

    registry.addEndpoint("/ws")
            .setAllowedOrigins("http://localhost:3000")
            .withSockJS();
  }

  @Override
  public void configureMessageBroker(MessageBrokerRegistry config){
    config.enableSimpleBroker("/topic", "/queue");
    config.setApplicationDestinationPrefixes("/app");
    config.setUserDestinationPrefix("/user");
  }

  @Override
  public void configureClientInboundChannel(final ChannelRegistration registration) {
    registration.interceptors(new ChannelInterceptor() {
      @Override
      public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
          String authHeader = accessor.getFirstNativeHeader("Authorization");
          if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
              String token = authHeader.substring(7);
              Algorithm algorithm = Algorithm.HMAC256("bHqf1AMjdbSSKn3MlGJW//yHf+RCW4BQkNcrQ0ifZ/EBt+7ixKFvjPtYgU6B3ZkYKp3H0hlVTezWfgr5cJTN3A=="); // Replace "secret" with your secret key
              JWTVerifier verifier = JWT.require(algorithm).build();
              DecodedJWT jwt = verifier.verify(token);
              String username = jwt.getSubject();

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
        }
        return message;
      }
    });
  }
}