package ru.vlad.websocketchat.client;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChatMessage {

    private MessageType type;
    private String message;
    private String user;

    public enum MessageType {
        CHAT, JOIN, LEAVE
    }

    public ChatMessage(MessageType type, String message, String user) {
        this.type = type;
        this.message = message;
        this.user = user;
    }
}
