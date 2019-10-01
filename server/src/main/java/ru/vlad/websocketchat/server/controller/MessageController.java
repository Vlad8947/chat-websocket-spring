package ru.vlad.websocketchat.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.vlad.websocketchat.server.database.service.MessageService;
import ru.vlad.websocketchat.server.model.ChatMessage;

import java.util.List;

@RestController
@RequestMapping(path = "/message-api")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @GetMapping(path = "/get-all")
    public List<ChatMessage> getAll() {
        return messageService.allChatMessage();
    }

    @GetMapping(path = "/get-all/{login}")
    public List<ChatMessage> getAllByLogin(@PathVariable("login") String login) {
        return messageService.allChatMessageByLogin(login);
    }

}
