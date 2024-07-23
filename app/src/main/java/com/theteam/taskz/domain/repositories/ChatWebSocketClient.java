package com.theteam.taskz.domain.repositories;

import com.theteam.taskz.domain.entities.Message;
import com.theteam.taskz.presentation.viewmodels.ChatsViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class ChatWebSocketClient {
    private OkHttpClient client;
    private WebSocket webSocket;
    private ChatsViewModel chatsViewModel;

    public ChatWebSocketClient(ChatsViewModel chatsViewModel) {
        client = new OkHttpClient();
        this.chatsViewModel = chatsViewModel;
    }

    public void connect(String url) {
        Request request = new Request.Builder().url(url).build();
        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                super.onOpen(webSocket, response);
                // Connection opened
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                super.onMessage(webSocket, text);
                // Handle incoming message
                handleNewMessage(text);
            }

            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
                super.onMessage(webSocket, bytes);
                // Handle incoming message
                handleNewMessage(bytes.utf8());
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                super.onClosing(webSocket, code, reason);
                webSocket.close(1000, null);
                // Connection closing
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                super.onClosed(webSocket, code, reason);
                // Connection closed
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                super.onFailure(webSocket, t, response);
                // Connection failed
            }
        });
    }

    public void disconnect() {
        if (webSocket != null) {
            webSocket.close(1000, null);
        }
    }

    private void handleNewMessage(String message) {
        final ArrayList<Message> messages = new ArrayList<>();
        try {
            final JSONArray messagesArray = new JSONArray(message);
            for(int i = 0; i<messagesArray.length(); i++){
                final Message chatMessage = Message.fromJson(messagesArray.getJSONObject(i).toString());
                messages.add(chatMessage);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        chatsViewModel.setMessages(messages);
    }
}

