package cs.sit.ScooopServerUltimatum.Controller;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

@ServerEndpoint("/ws")
public class OrderSocket {
    @OnOpen
    public void open(Session session) throws IOException, EncodeException {}

    @OnClose
    public void close(Session session) throws IOException, EncodeException {}

    @OnMessage
    public void handleMessage(String message, Session session) throws IOException, EncodeException {
        for(Session peer : session.getOpenSessions())
        {
            peer.getBasicRemote().sendText(message);
        }
    }
}