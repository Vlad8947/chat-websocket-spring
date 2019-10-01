package ru.vlad.websocketchat.server.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.vlad.websocketchat.server.database.entity.MessageTypeEntity;

import java.util.Optional;

@Repository
public interface MessageTypeRepository extends JpaRepository<MessageTypeEntity, Long> {

    Optional<MessageTypeEntity> findFirstByName(String name);
}
