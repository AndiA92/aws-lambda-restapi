package io.github.AndiA92.aws.lambda.restapi.dispatcher;

import io.github.AndiA92.aws.lambda.restapi.websockets.JSONWebSocketHandler;
import lombok.extern.log4j.Log4j;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONObject;

import static spark.Spark.*;

@Log4j
public class MessageDispatcher {

    private static Map<Session, String> userUsernameMap = new ConcurrentHashMap<>();

    private static int nextUserNumber;

    public static void main(String[] args) {
        staticFiles.location("/public"); //index.html is served at localhost:4567 (default port)
        staticFiles.expireTime(600);
        webSocket("/chat", JSONWebSocketHandler.class);
        init();
    }

    public static void registerUser(Session session, String username) {
        userUsernameMap.put(session, username + nextUserNumber++);
    }

    public static void unregisterUser(Session session) {
        userUsernameMap.remove(session);
    }


    public static void dispatch(String message) {
        userUsernameMap.keySet().stream().filter(Session::isOpen).forEach(session -> {
            try {
                session.getRemote()
                       .sendString(String.valueOf(new JSONObject()
                               .put("data", message)
                       ));
            } catch (Exception e) {
                log.info("An error occurred: ", e);
            }
        });
    }
}
