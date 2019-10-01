package ru.vlad.websocketchat.server.database.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.vlad.websocketchat.server.database.entity.UserEntity;
import ru.vlad.websocketchat.server.database.repository.UserRepository;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean existByLogin(String login) {
        Optional<UserEntity> optionalUser = userRepository.findFirstByLogin(login);
        return optionalUser.isPresent();
    }
}
