package io.github.AndiA92.aws.lambda.restapi.dispatcher;

import io.github.AndiA92.aws.lambda.restapi.websockets.JSONWebSocketHandler;
import lombok.extern.log4j.Log4j;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
//
//import org.apache.log4j.Level;
//import org.apache.log4j.Logger;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONObject;

import static spark.Spark.*;

@Slf4j
public class MessageDispatcher {

    private static Map<Session, String> userUsernameMap = new ConcurrentHashMap<>();

    private static int nextUserNumber;

    public static void main(String[] args) {
        webSocket("/lambda", JSONWebSocketHandler.class);
        init();
        post("/servers", (req, resp) -> {
            log.info("Message received from : " + req.ip());
            dispatch(req.body());
            return new JSONObject("{status: 'success'}");
        });
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
                               .put("userMessage", message)
                       ));
            } catch (Exception e) {
                log.error("An error occurred: ", e);
            }
        });
    }
}
