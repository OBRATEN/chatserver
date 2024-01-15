package org.stuchat.chatserver;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.RestTemplateXhrTransport;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import org.stuchat.chatserver.requests.SigninRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

class MyStompSessionHandler extends StompSessionHandlerAdapter {

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        System.out.println("Connected "+session.getSessionId());
    }

    @Override
    public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
        System.out.println(session.getSessionId()+command.getMessageType().toString()+exception.getMessage());
    }

    @Override
    public void handleTransportError(StompSession session, Throwable exception) {
        System.out.println(session.getSessionId()+exception.getMessage());
    }
}

public class Testing {

    public static final ObjectMapper om = new ObjectMapper();
    public static void main(String[] args) throws ExecutionException, InterruptedException, JsonProcessingException {
        WebSocketClient client = new StandardWebSocketClient();
        WebSocketStompClient stomp = new WebSocketStompClient(client);
        stomp.setMessageConverter(new StringMessageConverter());
        String url = "ws://192.168.101.28:8189/ws";
        StompSessionHandler handler = new MyStompSessionHandler();
        StompHeaders connectHeaders = new StompHeaders();
        connectHeaders.add("login", "test1");
        connectHeaders.add("passcode", "test");
        stomp.connect(url, new WebSocketHttpHeaders(), connectHeaders, new MyStompSessionHandler());
    }
}
