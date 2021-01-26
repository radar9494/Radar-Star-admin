package com.liuqi.third.wsclient;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

/**
 * @author tanyan
 * @create 2020-01=07
 * @description
 */
public class SocketClient extends WebSocketClient {

    public SocketClient(URI serverURI) {
        super(serverURI);
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        System.out.println("-open->"+serverHandshake.getHttpStatus());
    }

    @Override
    public void onMessage(String s) {
        System.out.println("-message->"+s);
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        System.out.println("-close->"+s);
    }

    @Override
    public void onError(Exception e) {
        System.out.println("-erroe->"+e.getMessage());
    }
}
