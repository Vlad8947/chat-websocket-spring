package ru.vlad.websocketchat.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import ru.vlad.websocketchat.server.interceptor.ChatHandshakeInterceptor;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    public static final String END_POINT = "/ws";
    public static final String APP_DEST_PREF = "/app";
    public static final String DEST_PREF = "/topic";

    @Bean
    public ChatHandshakeInterceptor chatHandshakeInterceptor() {
        return new ChatHandshakeInterceptor();
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(END_POINT).addInterceptors(chatHandshakeInterceptor()).withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes(APP_DEST_PREF);
        registry.enableSimpleBroker(DEST_PREF);
    }

}
