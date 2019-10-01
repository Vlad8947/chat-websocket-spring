package ru.vlad.websocketchat.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import ru.vlad.websocketchat.server.database.entity.MessageTypeEntity;
import ru.vlad.websocketchat.server.database.entity.UserEntity;
import ru.vlad.websocketchat.server.database.repository.MessageRepository;
import ru.vlad.websocketchat.server.database.repository.MessageTypeRepository;
import ru.vlad.websocketchat.server.database.repository.UserRepository;
import ru.vlad.websocketchat.server.interceptor.ChatHandshakeInterceptor;
import ru.vlad.websocketchat.server.model.ChatMessage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static ru.vlad.websocketchat.server.model.ChatMessage.MessageType;

@Component
public class DataBaseRunner implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(ChatHandshakeInterceptor.class);

    private static final String LOGIN_LIST_FILE_NAME = "login.lst";

    private UserRepository userRepository;
    private MessageRepository messageRepository;
    private MessageTypeRepository messageTypeRepository;

    @Autowired
    public DataBaseRunner(UserRepository userRepository, MessageRepository messageRepository, MessageTypeRepository messageTypeRepository) {
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
        this.messageTypeRepository = messageTypeRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        setUsers();
        setMessageTypes();
    }

    private void setUsers() {
        File loginListFile = new File(LOGIN_LIST_FILE_NAME);
        if (!loginListFile.exists()) {
            try {
                loginListFile.createNewFile();
            } catch (IOException e) {
                logger.error(e.getMessage());
                return;
            }
        }
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(LOGIN_LIST_FILE_NAME))) {
            List<UserEntity> userList = new ArrayList<>();
            for (String login = bufferedReader.readLine(); login != null; login = bufferedReader.readLine()) {
                if (login.trim().isEmpty()) {
                    continue;
                }
                UserEntity userEntity = new UserEntity();
                userEntity.setLogin(login);
                userList.add(userEntity);
            }
            if (userList.isEmpty()) {
                logger.error("File login.lst is empty! Need to add login strings.");
            }
            userRepository.saveAll(userList);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private void setMessageTypes() {
        MessageType[] messageTypes = MessageType.values();
        List<MessageTypeEntity> typeEntities = new ArrayList<>();
        for (ChatMessage.MessageType type : messageTypes) {
            MessageTypeEntity messageTypeEntity = new MessageTypeEntity();
            messageTypeEntity.setName(type.name());
            typeEntities.add(messageTypeEntity);
        }
        messageTypeRepository.saveAll(typeEntities);
    }

}
