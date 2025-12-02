package com.rehancode.chatapp.Configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.rehancode.chatapp.ChatHandler.ChatHandler;
import com.rehancode.chatapp.Interceptor.JwtHandshakeInterceptor;

@Configuration
@EnableWebSocket   // ✅ THIS WAS MISSING
public class WebSocketConfig implements WebSocketConfigurer {

    private final ChatHandler chatHandler;
    private final JwtHandshakeInterceptor jwtHandshakeInterceptor;

    public WebSocketConfig(ChatHandler chatHandler,
                           JwtHandshakeInterceptor jwtHandshakeInterceptor) {
        this.chatHandler = chatHandler;
        this.jwtHandshakeInterceptor = jwtHandshakeInterceptor;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {

        System.out.println(">>> REGISTERING WEBSOCKET HANDLER <<<");

        registry.addHandler(chatHandler, "/ws/chat")
                .setAllowedOriginPatterns("*")   // ✅ REQUIRED FOR SPRING BOOT 3
                .addInterceptors(jwtHandshakeInterceptor);
    }
}
