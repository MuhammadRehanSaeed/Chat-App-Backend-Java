package com.rehancode.chatapp.Configuration;

import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.rehancode.chatapp.ChatHandler.ChatHandler;
import com.rehancode.chatapp.Interceptor.JwtHandshakeInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
@EnableWebSocket   // ✅ THIS WAS MISSING
public class WebSocketConfig implements WebSocketConfigurer {

    private final ChatHandler chatHandler;
    private final JwtHandshakeInterceptor jwtHandshakeInterceptor;
      private static final Logger logger =
            LoggerFactory.getLogger(WebSocketConfig.class);

    public WebSocketConfig(ChatHandler chatHandler,
                           JwtHandshakeInterceptor jwtHandshakeInterceptor) {
        this.chatHandler = chatHandler;
        this.jwtHandshakeInterceptor = jwtHandshakeInterceptor;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
         logger.info("Registering WebSocket Handler at /ws/chat");


        System.out.println(">>> REGISTERING WEBSOCKET HANDLER <<<");

        registry.addHandler(chatHandler, "/ws/chat")
                .setAllowedOriginPatterns("*")   // ✅ REQUIRED FOR SPRING BOOT 3
                .addInterceptors(jwtHandshakeInterceptor);
                  logger.info("WebSocket Handler Registered Successfully");
    }
}
