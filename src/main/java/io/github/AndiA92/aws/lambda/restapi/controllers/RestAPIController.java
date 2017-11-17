package io.github.AndiA92.aws.lambda.restapi.controllers;

import lombok.extern.log4j.Log4j;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/lambda")
@RestController
@Log4j
public class RestAPIController {

    private final JSONWebSocket socket = new JSONWebSocket();

    @RequestMapping(value = "/servers/", method = RequestMethod.POST)
    public String postMessage(String json) {
        socket.onMessage(json);
        return "hello";
    }

    @WebSocket
    private final class JSONWebSocket {

        @OnWebSocketMessage
        void onMessage(String message) {

        }
    }

}
