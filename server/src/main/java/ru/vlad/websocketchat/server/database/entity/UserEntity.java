package ru.vlad.websocketchat.server.database.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "Users")
@Data
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String login;

    @Column(nullable = false)
    @OneToMany(mappedBy="user", cascade = { CascadeType.REMOVE, CascadeType.PERSIST})
    private List<MessageEntity> messages;

}
