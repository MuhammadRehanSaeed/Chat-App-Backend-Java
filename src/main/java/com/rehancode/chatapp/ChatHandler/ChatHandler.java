package com.rehancode.chatapp.ChatHandler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rehancode.chatapp.Entity.Chat;
import com.rehancode.chatapp.Repository.ChatRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component
public class ChatHandler extends TextWebSocketHandler {
     private static final Logger logger =
            LoggerFactory.getLogger(ChatHandler.class);

    private final ChatRepository chatRepository;
    private static final List<WebSocketSession> sessions = new ArrayList<>();
    private static final Map<String, WebSocketSession> userSessionMap = new HashMap<>();
    private final ObjectMapper mapper = new ObjectMapper();

    public ChatHandler(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }
    private void broadcastOnlineUsers() throws Exception {
    List<String> onlineUsers = new ArrayList<>(userSessionMap.keySet());
    Map<String, Object> payload = new HashMap<>();
    payload.put("type", "online_users");
    payload.put("users", onlineUsers);

    String json = mapper.writeValueAsString(payload);
    for (WebSocketSession s : sessions) {
        if (s.isOpen()) s.sendMessage(new TextMessage(json));
    }
      logger.info("Online Users Updated: {}", onlineUsers);
}

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String username = (String) session.getAttributes().get("username");
        sessions.add(session);
        userSessionMap.put(username, session);
        System.out.println(username + " connected");
          logger.info("User Connected: {}", username);
         broadcastOnlineUsers(); // send updated list
    }

  @Override
protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

    String payload = message.getPayload();
    Map<String, String> msg = mapper.readValue(payload, Map.class);

    String sender = (String) session.getAttributes().get("username");
    if(sender == null){
         logger.warn("Connection closed due to missing username");
        session.close();
        return;
    }

    String content = msg.get("message");
    String type = msg.getOrDefault("type", "global");
    String recipient = msg.get("recipient");

       logger.debug("Message Received | From: {} | Type: {} | To: {} | Msg: {}",
                sender, type, recipient, content);

    Chat chat = new Chat();
    chat.setSender(sender);
    chat.setMessage(content);
    chat.setRecipient(recipient);
    chat.setType(type);
    chat.setTimestamp(LocalDateTime.now());
    chatRepository.save(chat);

       logger.info("Message Saved | From: {} | Type: {} | To: {}",
                sender, type, recipient);

    if ("private".equalsIgnoreCase(type) && recipient != null) {
        WebSocketSession recipientSession = userSessionMap.get(recipient);

        if (recipientSession == null || !recipientSession.isOpen()) {
              logger.warn("Private Message Failed | Recipient Offline | From: {} | To: {}",
                        sender, recipient);
            Map<String, Object> errorPayload = new HashMap<>();
            errorPayload.put("type", "error");
            errorPayload.put("message", "User is offline");
            session.sendMessage(new TextMessage(mapper.writeValueAsString(errorPayload)));
            return;
        }

        Map<String, Object> messagePayload = new HashMap<>();
        messagePayload.put("type", "private_message");
        messagePayload.put("from", sender);
        messagePayload.put("message", content);
        messagePayload.put("timestamp", LocalDateTime.now().toString());
        messagePayload.put("notification", true);

        recipientSession.sendMessage(new TextMessage(mapper.writeValueAsString(messagePayload)));

        Map<String, Object> senderPayload = new HashMap<>();
        senderPayload.put("type", "confirmation");
        senderPayload.put("to", recipient);
        senderPayload.put("message", content);

        session.sendMessage(new TextMessage(mapper.writeValueAsString(senderPayload)));
           logger.info("Private Message Sent | From: {} | To: {} | Msg: {}",
                    sender, recipient, content);


    } else {
        synchronized (sessions) {
            for (WebSocketSession s : sessions) {
                if (s.isOpen()) {
                    Map<String, Object> broadcastPayload = new HashMap<>();
                    broadcastPayload.put("type", "message");
                    broadcastPayload.put("from", sender);
                    broadcastPayload.put("message", content);
                    broadcastPayload.put("timestamp", LocalDateTime.now().toString());
                    broadcastPayload.put("notification", true); 

                    s.sendMessage(new TextMessage(mapper.writeValueAsString(broadcastPayload)));
                }
            }
        }
           logger.info("Global Message Broadcast | From: {} | Msg: {}",
                    sender, content);
    }
}


    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String username = (String) session.getAttributes().get("username");
        sessions.remove(session);
        userSessionMap.remove(username);
        System.out.println(username + " disconnected");
           logger.info("User Disconnected: {} | Status: {}", username, status);
           broadcastOnlineUsers();
    }
}
