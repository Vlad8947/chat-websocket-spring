package ru.vlad.websocketchat.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import org.springframework.web.socket.sockjs.frame.Jackson2SockJsMessageCodec;

import java.io.*;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import static org.springframework.messaging.simp.stomp.StompHeaders.LOGIN;

public class ClientRunner implements Runnable {

    private static final String APP_PROPERTIES_FILE_NAME = "app.properties";
    private static final String PORT = "port";
    private static final String ADDRESS = "address";

    private static final String END_POINT = "ws";
    private static final String SUBSCRIBE_PATH = "/app/chat/sendMessage";
    private static final String QUITE_COMMAND = "-q";

    private String address;
    private String port;
    private String login;

    private ClientHandler clientHandler = new ClientHandler();
    private StompHeaders stompHeaders = new StompHeaders();
    private ListenableFuture<StompSession> futures;

    public ClientRunner() {
        initProperties();
        WebSocketStompClient stompClient = getWebSocketStompClient();
        WebSocketHttpHeaders webHeaders = new WebSocketHttpHeaders();
        webHeaders.add(LOGIN, login);
        stompHeaders.setLogin(login);
        stompHeaders.setDestination("/topic/public");
        String url = "{endPoint}://{host}:{port}/ws";
        futures = stompClient.connect(url, webHeaders, stompHeaders, clientHandler, END_POINT, address, port);
    }

    private void initProperties() {
        File propFile = new File(APP_PROPERTIES_FILE_NAME);
        String propStr = String.format("(%s, %s, %s)", LOGIN, ADDRESS, PORT);
        if (!propFile.exists()) {
            try {
                propFile.createNewFile();
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }

            System.err.println("Enter the parameters in app.properties " + propStr);;
            System.exit(1);
        }

        try (FileInputStream fileInputStream = new FileInputStream(APP_PROPERTIES_FILE_NAME)) {
            Properties appProps = new Properties();
            appProps.load(fileInputStream);
            login = appProps.getProperty(LOGIN);
            port = appProps.getProperty(PORT);
            address = appProps.getProperty(ADDRESS);

            boolean isNullProps = login == null || port == null || address == null;
            if (isNullProps) {
                System.err.println("Property(ies) is not exist in the file app.properties. Need " + propStr);
                System.exit(1);
            }
            boolean isEmptyProps = login.trim().isEmpty() || port.trim().isEmpty() || address.trim().isEmpty();
            if (isEmptyProps) {
                System.err.println("Empty field(s) in the file app.properties");
                System.exit(1);
            }

        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

    }

    @Override
    public void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            StompSession stompSession = futures.get();
            stompSession.subscribe(stompHeaders, clientHandler);
            ObjectMapper mapper = new ObjectMapper();

            System.out.printf("Enter \"%s\" for exit. \n", QUITE_COMMAND);

            ChatMessage chatMessage;
            String jsonMessage;
            boolean isQuite = false;
            while(!isQuite) {
                String message = reader.readLine();
                if (message.trim().equals(QUITE_COMMAND)) {
                    isQuite = true;
                    continue;
                }
                chatMessage = new ChatMessage(ChatMessage.MessageType.CHAT, message, login);
                jsonMessage = mapper.writeValueAsString(chatMessage);
                stompSession.send(SUBSCRIBE_PATH, jsonMessage.getBytes());
            }
            stompSession.disconnect();

        } catch (ExecutionException e) {
            System.err.println("ExecutionException. Maybe your login is wrong");
            System.err.println(e.getMessage());
        } catch (InterruptedException | IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private WebSocketStompClient getWebSocketStompClient() {
        Transport webSocketTransport = new WebSocketTransport(new StandardWebSocketClient());
        List<Transport> transports = Collections.singletonList(webSocketTransport);
        SockJsClient sockJsClient = new SockJsClient(transports);
        sockJsClient.setMessageCodec(new Jackson2SockJsMessageCodec());
        return new WebSocketStompClient(sockJsClient);
    }

}
