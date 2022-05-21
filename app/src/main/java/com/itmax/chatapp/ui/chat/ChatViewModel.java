package com.itmax.chatapp.ui.chat;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.itmax.chatapp.AppConfig;
import com.itmax.chatapp.data.Result;
import com.itmax.chatapp.data.model.Chat;
import com.itmax.chatapp.data.model.Message;
import com.itmax.chatapp.data.model.User;
import com.itmax.chatapp.data.repositories.ChatRepository;
import com.itmax.chatapp.data.repositories.LoginRepository;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import tech.gusavila92.websocketclient.WebSocketClient;

public class ChatViewModel extends ViewModel {

    private WebSocketClient webSocketClient;
    private final ChatRepository chatRepository;
    private final MutableLiveData<List<Message>> messagesList;
    private final MutableLiveData<Chat> chatInfo;

    public ChatViewModel(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
        messagesList = new MutableLiveData<>();
        chatInfo = new MutableLiveData<>();
        this.createWebSocketClient();
    }

    private void createWebSocketClient() {
        URI uri;
        try {
            uri = new URI(AppConfig.WS_URL);
        }
        catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        webSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen() {
                Log.i("WS", "onOpen");
            }

            @Override
            public void onTextReceived(String message) {
                Log.i("WS", "onTextReceived: " + message);

                try {
                    JSONObject messageReceived = new JSONObject(message);

                    List<Message> newMessagesList = new ArrayList(messagesList.getValue());
                    newMessagesList.add(new Message(messageReceived));
                    messagesList.postValue(newMessagesList);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onBinaryReceived(byte[] data) {
                Log.i("WS", "onBinaryReceived");
            }

            @Override
            public void onPingReceived(byte[] data) {
                Log.i("WS", "onPingReceived");
            }

            @Override
            public void onPongReceived(byte[] data) {
                Log.i("WS", "onPongReceived");
            }

            @Override
            public void onException(Exception ex) {
                Log.e("WS", ex.getMessage());
            }

            @Override
            public void onCloseReceived() {
                Log.i("WS", "onCloseReceived");
            }
        };

        webSocketClient.setConnectTimeout(10000);
        webSocketClient.setReadTimeout(60000);
        webSocketClient.addHeader("Authorization", "Bearer " + LoginRepository.getInstance().getLoggedInUser().getToken());
        webSocketClient.enableAutomaticReconnection(5000);
        webSocketClient.connect();
    }

    public LiveData<Chat> getChatInfo() {
        return this.chatInfo;
    }

    public LiveData<List<Message>> getChatMessages() {
        return this.messagesList;
    }

    public void getChatInfo(String chatId) {
        Thread loadChatInfo = new Thread(() -> {
            Result<List<Message>> result = chatRepository.getChatInfo(chatId);

            if (result instanceof Result.Success) {
                Chat data = ((Result.Success<Chat>) result).getData();
                chatInfo.postValue(data);
            }
        });

        loadChatInfo.start();
    }

    public void getChatMessages(String chatId) {
        Thread loadChatMessages = new Thread(() -> {
            Result<List<Message>> result = chatRepository.getChatMessages(chatId);

            if (result instanceof Result.Success) {
                List<Message> data = ((Result.Success<List<Message>>) result).getData();
                messagesList.postValue(data);
            }
        });

        loadChatMessages.start();
    }

    public void sendMessage(String chatId, String text) {
        Thread sendMessage = new Thread(() -> {
            Result<Message> result = chatRepository.sendMessage(
                    chatId,
                    text
            );

            if (result instanceof Result.Success) {
                Message data = ((Result.Success<Message>) result).getData();

                // Create new List so we change reference and force messages list re-render
                List<Message> newMessagesList = new ArrayList<>(messagesList.getValue());
                newMessagesList.add(data);

                webSocketClient.send(text);

                messagesList.postValue(newMessagesList);
            }
        });

        sendMessage.start();
    }
}