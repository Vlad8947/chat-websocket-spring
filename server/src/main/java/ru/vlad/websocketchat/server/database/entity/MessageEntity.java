package ru.vlad.websocketchat.server.database.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Calendar;

@Entity
@Table(name = "Messages")
@Data
public class MessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    private UserEntity user;

    @Column(nullable = false)
    private String content;

    @ManyToOne
    private MessageTypeEntity messageType;

    @Basic
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Calendar calendarTimestamp;

    public static MessageEntity build(UserEntity userEntity, MessageTypeEntity typeEntity, String content) {
        MessageEntity messageEntity = new MessageEntity();
        messageEntity.setUser(userEntity);
        messageEntity.setContent(content);
        messageEntity.setMessageType(typeEntity);
        messageEntity.setCalendarTimestamp(Calendar.getInstance());
        return messageEntity;
    }

}
