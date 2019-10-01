package ru.vlad.websocketchat.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

import java.io.IOException;
import java.lang.reflect.Type;

import static ru.vlad.websocketchat.client.ChatMessage.MessageType.*;

public class ClientHandler extends StompSessionHandlerAdapter {

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        System.out.println("Connected");
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return byte[].class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        ChatMessage message = null;
        try {
            message = new ObjectMapper().readValue((byte[]) payload, ChatMessage.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ChatMessage.MessageType type = message.getType();
        String user = message.getUser();
        String consoleMessage = null;
        if (type.equals(CHAT)) {
            consoleMessage = String.format("%s: %s", user, message.getMessage());
        } else if (type.equals(JOIN)) {
            consoleMessage = String.format("User %s joined", user);
        } else if (type.equals(LEAVE)) {
            consoleMessage = String.format("User %s leaved", user);
        }
        System.out.println(consoleMessage);
    }



}
