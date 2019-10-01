package ru.vlad.websocketchat.server.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import ru.vlad.websocketchat.server.database.service.UserService;

import java.util.Map;

import static org.springframework.messaging.simp.stomp.StompHeaders.LOGIN;

public class ChatHandshakeInterceptor implements HandshakeInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(ChatHandshakeInterceptor.class);
    @Autowired
    private UserService userService;

    @Override
    public boolean beforeHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Map<String, Object> map) throws Exception {
        try {
            String login = serverHttpRequest.getHeaders().get(LOGIN).get(0);
            if (login == null || login.trim().isEmpty()) {
                logger.info("Login attribute is null/empty");
                return false;
            }
            if (!userService.existByLogin(login)) {
                logger.info(String.format("User \"%s\" is not exist", login));
                return false;
            }
            String logStr = String.format("User \"%s\" doing handshake", login);
            logger.info(logStr);
            return true;

        } catch (NullPointerException | IndexOutOfBoundsException ex) {
            logger.info("Login attribute not found");
        }
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Exception e) {
//        logger.info("Call afterHandshake");
    }
}
