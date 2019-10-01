package ru.vlad.websocketchat.server.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import ru.vlad.websocketchat.server.database.service.MessageService;
import ru.vlad.websocketchat.server.model.ChatMessage;

@Controller
@MessageMapping("/chat")
public class ChatController {

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);
    public static final String TOPIC_PUBLIC = "/topic/public";

    @Autowired
    private MessageService messageService;

    @MessageMapping("/sendMessage")
    @SendTo(TOPIC_PUBLIC)
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        boolean isSaved = messageService.save(chatMessage);
        if (!isSaved) {
            return null;
        }
        logPublicMessage(chatMessage);
        return chatMessage;
    }

    private void logPublicMessage(ChatMessage chatMessage) {
        logger.info(String.format("PUBLIC %s: %s", chatMessage.getUser(), chatMessage.getMessage()));
    }

}
