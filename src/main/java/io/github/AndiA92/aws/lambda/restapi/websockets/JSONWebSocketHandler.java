package io.github.AndiA92.aws.lambda.restapi.websockets;

import io.github.AndiA92.aws.lambda.restapi.dispatcher.MessageDispatcher;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

@WebSocket
public class JSONWebSocketHandler {

    private static final String USER = "user";

    @OnWebSocketConnect
    public void onConnect(Session user) throws Exception {
        MessageDispatcher.registerUser(user, USER);
    }

    @OnWebSocketClose
    public void onClose(Session user, int statusCode, String reason) {
        MessageDispatcher.unregisterUser(user);
    }

    @OnWebSocketMessage
    public void onMessage(Session user, String message) {
        MessageDispatcher.dispatch(message);
    }
}
