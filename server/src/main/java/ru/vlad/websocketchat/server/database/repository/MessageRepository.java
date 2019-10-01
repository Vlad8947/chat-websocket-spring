package ru.vlad.websocketchat.server.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.vlad.websocketchat.server.database.entity.MessageEntity;
import ru.vlad.websocketchat.server.database.entity.UserEntity;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, Long> {

    List<MessageEntity> findByUser(UserEntity user);

}
