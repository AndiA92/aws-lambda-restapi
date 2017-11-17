package io.github.AndiA92.aws.lambda.restapi.controllers;

import io.github.AndiA92.aws.lambda.restapi.websockets.JSONWebSocketHandler;
import lombok.extern.log4j.Log4j;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/lambda")
@RestController
@Log4j
public class RestAPIController {

    private final JSONWebSocketHandler socket = new JSONWebSocketHandler();

    @RequestMapping(value = "/servers", method = RequestMethod.POST)
    public String postMessage(String json) {
        socket.onMessage(json);
        return "hello";
    }
}
