package ru.vlad.websocketchat.server.database.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "MessageTypes")
@Data
public class MessageTypeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy="messageType", cascade = { CascadeType.REMOVE, CascadeType.PERSIST})
    private List<MessageEntity> messages;

}
