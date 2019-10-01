package ru.vlad.websocketchat.server.database.service;

import ru.vlad.websocketchat.server.database.entity.MessageEntity;
import ru.vlad.websocketchat.server.model.ChatMessage;

import java.util.List;

public interface MessageService {

    List<MessageEntity> all();
    List<ChatMessage> allChatMessage();
    List<MessageEntity> allByLogin(String login);
    List<ChatMessage> allChatMessageByLogin(String login);
    boolean save(ChatMessage message);
}
