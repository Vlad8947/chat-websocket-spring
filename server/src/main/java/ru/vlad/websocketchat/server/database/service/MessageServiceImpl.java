package ru.vlad.websocketchat.server.database.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.vlad.websocketchat.server.database.entity.MessageEntity;
import ru.vlad.websocketchat.server.database.entity.MessageTypeEntity;
import ru.vlad.websocketchat.server.database.entity.UserEntity;
import ru.vlad.websocketchat.server.database.repository.MessageRepository;
import ru.vlad.websocketchat.server.database.repository.MessageTypeRepository;
import ru.vlad.websocketchat.server.database.repository.UserRepository;
import ru.vlad.websocketchat.server.model.ChatMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageRepository messageRepo;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private MessageTypeRepository messageTypeRepository;

    @Override
    public List<MessageEntity> all() {
        return messageRepo.findAll();
    }

    @Override
    public List<ChatMessage> allChatMessage() {
        List<MessageEntity> messageEntityList = messageRepo.findAll();
        return entityListToChatMessage(messageEntityList);
    }

    private List<ChatMessage> entityListToChatMessage(List<MessageEntity> entityList) {
        List<ChatMessage> chatMessages = new ArrayList<>();
        entityList.forEach(messageEntity -> {
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setUser(messageEntity.getUser().getLogin());
            ChatMessage.MessageType messageType = ChatMessage.MessageType.valueOf(messageEntity.getMessageType().getName());
            chatMessage.setType(messageType);
            chatMessage.setMessage(messageEntity.getContent());
            chatMessages.add(chatMessage);
        });
        return chatMessages;
    }

    @Override
    public List<MessageEntity> allByLogin(String login) {
        Optional<UserEntity> optionalUser = userRepo.findFirstByLogin(login);
        return optionalUser.map(userEntity -> messageRepo.findByUser(userEntity)).orElse(null);
    }

    @Override
    public List<ChatMessage> allChatMessageByLogin(String login) {
        List<MessageEntity> messageEntityList = allByLogin(login);
        if (messageEntityList == null || messageEntityList.isEmpty()) {
            return null;
        }
        return entityListToChatMessage(messageEntityList);
    }

    @Override
    public boolean save(ChatMessage chatMessage) {
        Optional<UserEntity> userOptional = userRepo.findFirstByLogin(chatMessage.getUser());
        Optional<MessageTypeEntity> messageTypeOptional = messageTypeRepository.findFirstByName(chatMessage.getType().name());
        if (!userOptional.isPresent() || !messageTypeOptional.isPresent() || chatMessage.getMessage().trim().isEmpty()) {
            return false;
        }
        MessageEntity messageEntity = MessageEntity.build(userOptional.get(), messageTypeOptional.get(), chatMessage.getMessage());
        messageRepo.save(messageEntity);
        return true;
    }

}
