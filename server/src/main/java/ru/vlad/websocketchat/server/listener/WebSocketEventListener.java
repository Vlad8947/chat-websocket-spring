package ru.vlad.websocketchat.server.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import ru.vlad.websocketchat.server.controller.ChatController;
import ru.vlad.websocketchat.server.database.service.MessageServiceImpl;
import ru.vlad.websocketchat.server.model.ChatMessage;

import java.util.LinkedList;
import java.util.Map;

@Component
public class WebSocketEventListener {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);
    @Autowired
    private SimpMessageSendingOperations sendingOperations;
    @Autowired
    private MessageServiceImpl messageService;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        Map<String, Object> map = headerAccessor.getMessageHeaders();

        GenericMessage genericMessage = (GenericMessage) map.get("simpConnectMessage");
        Map<String, LinkedList<String>> nativeHeaders = (Map<String, LinkedList<String>>) genericMessage.getHeaders().get("nativeHeaders");
        String login = nativeHeaders.get("login").getFirst();
        Map<String, String> sessionAttr = (Map<String, String>) genericMessage.getHeaders().get("simpSessionAttributes");
        if (login == null || login.trim().isEmpty()) {
            logger.info("Received a new web socket connection.");
            return;
        }
        sessionAttr.put("login", login);
        infoUserJoined(login);
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String login = (String) headerAccessor.getSessionAttributes().get("login");
        if(login != null) {
            infoUserLeave(login);
        }
    }

    private void infoUserJoined(String login) {
        logger.info("Received a new web-socket connection. User " + login);
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setType(ChatMessage.MessageType.JOIN);
        chatMessage.setUser(login);
        chatMessage.setMessage(String.format("User %s joined", login));
        messageService.save(chatMessage);
        sendingOperations.convertAndSend(ChatController.TOPIC_PUBLIC, chatMessage);
    }

    private void infoUserLeave(String login) {
        logger.info("User Disconnected : " + login);
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setType(ChatMessage.MessageType.LEAVE);
        chatMessage.setUser(login);
        chatMessage.setMessage(String.format("User %s leave", login));
        messageService.save(chatMessage);
        sendingOperations.convertAndSend(ChatController.TOPIC_PUBLIC, chatMessage);
    }

}
