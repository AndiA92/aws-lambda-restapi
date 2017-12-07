package io.github.andia92.restws.websockets;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@Slf4j
public class WebSocketBroadcasterTest {

    private static final String CHANNEL = "test1";

    private static final String URI = "localhost:4567/";

    private static final String WS_URI = "ws://" + URI + CHANNEL;

    private URI echoUri = new URI(WS_URI);

    private WebSocketBroadcaster webSocketBroadcaster;

    private WebSocketClient wsClient;

    private WebSocketTestImpl webSocket;

    public WebSocketBroadcasterTest() throws URISyntaxException {
    }

    @Before
    public void before() {
        webSocketBroadcaster = new WebSocketBroadcaster();
        webSocketBroadcaster.run(new String[]{CHANNEL});

        wsClient = new WebSocketClient();
        initWS();
    }

    private void initWS() {
        try {
            wsClient.start();
            webSocket = new WebSocketTestImpl();
            ClientUpgradeRequest request = new ClientUpgradeRequest();
            wsClient.connect(webSocket, echoUri, request);
            log.info("Connecting to: " + echoUri);
        } catch (Throwable t) {
            log.error("Exception occurred:", t.getMessage());
        }
    }

    @Test
    public void restApiCalled_MessageDispatchedToWS() throws InterruptedException {
        given().body("content")
               .when()
               .post("http://" + URI + "broadcast/" + CHANNEL)
               .then()
               .statusCode(200)
               .body("status", equalTo("success"));

        List<String> actual = new ArrayList<>();

        while (actual.size() == 0) {
            actual = webSocket.getMessages();
        }

        List<String> expected = Collections.singletonList("content");
        Assert.assertEquals(expected, actual);
    }

    @After
    public void after() throws Exception {
        webSocketBroadcaster.stop();
        wsClient.stop();
    }

    @WebSocket
    public static class WebSocketTestImpl {

        @Getter
        private final List<String> messages = new ArrayList<>();

        @OnWebSocketConnect
        public void onConnect(Session user) throws Exception {
        }

        @OnWebSocketClose
        public void onClose(Session user, int statusCode, String reason) {
        }

        @OnWebSocketMessage
        public void onMessage(Session user, String message) {
            messages.add(message);
        }
    }
}
