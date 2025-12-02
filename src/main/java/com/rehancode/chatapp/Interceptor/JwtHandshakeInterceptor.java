package com.rehancode.chatapp.Interceptor;

import com.rehancode.chatapp.JWT.JwtService;
import com.rehancode.chatapp.UserDetailsService.CustomUserDetailsService;

import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Component
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public JwtHandshakeInterceptor(JwtService jwtService,
                                   CustomUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) {

                                        System.out.println(">>> WS HANDSHAKE STARTED <<<");
    System.out.println("Handshake URL: " + request.getURI());

        String token = UriComponentsBuilder.fromUri(request.getURI())
                .build()
                .getQueryParams()
                .getFirst("token");

        if (token == null || token.isEmpty()) {
            reject(response);
            System.out.println("WS rejected: missing token");
            return false;
        }

        try {
            String username = jwtService.extractUsername(token);
            UserDetails userDetails =
                    userDetailsService.loadUserByUsername(username);

            if (jwtService.validateToken(token, userDetails)) {
                attributes.put("username", username);
                System.out.println("WS allowed for user: " + username);
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        reject(response);
        System.out.println("WS rejected: invalid token");
        return false;
    }
    

    private void reject(ServerHttpResponse response) {
        if (response instanceof ServletServerHttpResponse servlet) {
            servlet.getServletResponse().setStatus(HttpStatus.FORBIDDEN.value());
        } else {
            response.setStatusCode(HttpStatus.FORBIDDEN);
        }
    }
        @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response,
                               WebSocketHandler wsHandler,
                               Exception exception) {}
}


// public class JwtHandshakeInterceptor implements HandshakeInterceptor {

//     private final JwtService jwtService;

//     public JwtHandshakeInterceptor(JwtService jwtService) {
//         this.jwtService = jwtService;
//     }

//     @Override
//     public boolean beforeHandshake(ServerHttpRequest request,
//                                    ServerHttpResponse response,
//                                    WebSocketHandler wsHandler,
//                                    Map<String, Object> attributes) {

//         String token = UriComponentsBuilder.fromUri(request.getURI())
//                 .build()
//                 .getQueryParams()
//                 .getFirst("token");

//         if (token == null || token.isEmpty()) {
//             reject(response);
//             System.out.println("WebSocket connection rejected: missing token");
//             return false;
//         }

//         try {
//             String username = jwtService.extractUsername(token);
//             if (username != null && jwtService.isTokenValid(token, username)) {
//                 attributes.put("username", username);
//                 System.out.println("WebSocket handshake allowed for user: " + username);
//                 return true;
//             }
//         } catch (Exception e) {
//             e.printStackTrace();
//         }

//         reject(response);
//         System.out.println("WebSocket connection rejected: invalid token");
//         return false;
//     }

//     private void reject(ServerHttpResponse response) {
//         if (response instanceof ServletServerHttpResponse servlet) {
//             servlet.getServletResponse().setStatus(HttpStatus.FORBIDDEN.value());
//         } else {
//             response.setStatusCode(HttpStatus.FORBIDDEN);
//         }
//     }

//     @Override
//     public void afterHandshake(ServerHttpRequest request,
//                                ServerHttpResponse response,
//                                WebSocketHandler wsHandler,
//                                Exception exception) {}
// }
