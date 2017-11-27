package io.github.andia92.restws.websockets;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@WebSocket
public class WebSocketHandler {

    private static final String USER = "user";

    private Map<Session, String> userUsernameMap = new ConcurrentHashMap<>();

    private AtomicInteger nextUserNumber = new AtomicInteger(0);

    @OnWebSocketConnect
    public void onConnect(Session user) throws Exception {
        userUsernameMap.put(user, USER + nextUserNumber.getAndIncrement());
    }

    @OnWebSocketClose
    public void onClose(Session user, int statusCode, String reason) {
        userUsernameMap.remove(user);
    }

    @OnWebSocketMessage
    public void onMessage(Session user, String message) {
        dispatch(message);
    }

    public void dispatch(String message) {
        userUsernameMap.keySet()
                       .stream()
                       .filter(Session::isOpen)
                       .forEach(session -> {
                           try {
                               session.getRemote()
                                      .sendString(message);
                           } catch (Exception e) {
                               log.error("An error occurred: ", e);
                           }
                       });
    }


}
