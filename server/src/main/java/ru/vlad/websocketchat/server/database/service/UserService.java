package ru.vlad.websocketchat.server.database.service;

public interface UserService {

    boolean existByLogin(String login);

}
