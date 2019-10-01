package ru.vlad.websocketchat.server.model;

import lombok.Data;

@Data
public class ChatMessage {

    private MessageType type;
    private String message;
    private String user;

    public enum MessageType {
        CHAT, JOIN, LEAVE
    }

}
