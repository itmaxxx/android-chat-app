package com.itmax.chatapp.ui.chat;

import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.itmax.chatapp.AppConfig;
import com.itmax.chatapp.data.Result;
import com.itmax.chatapp.data.model.Chat;
import com.itmax.chatapp.data.model.Message;
import com.itmax.chatapp.data.model.Notification;
import com.itmax.chatapp.data.repositories.ChatRepository;
import com.itmax.chatapp.data.repositories.LoginRepository;
import com.itmax.chatapp.data.repositories.NotificationsRepository;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ChatViewModel extends ViewModel {

    private final ChatRepository chatRepository;
    private final MutableLiveData<List<Message>> messagesList;
    private final MutableLiveData<Chat> chatInfo;
    private Emitter.Listener chatMessagesListener;

    private Socket mSocket;
    {
        try {
            mSocket = IO.socket(AppConfig.WS_URL);
        } catch (URISyntaxException e) {
            Log.e("WebSocket", e.getMessage());
        }
    }

    public ChatViewModel(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
        messagesList = new MutableLiveData<>();
        chatInfo = new MutableLiveData<>();
    }

    public void listenForChatMessages(String chatId) {
        mSocket.connect();

        chatMessagesListener = args -> {
            try {
                String receivedData = args[0].toString();
                Log.i("WebSocket", "Got new chat '" + chatId + "' message: " + receivedData);

                JSONObject jsonData = new JSONObject(receivedData);

                Message receivedMessage = new Message(jsonData);

                // Add message to messages list
                if (receivedMessage.getChatId().equals(chatId)
                        && !receivedMessage.getIsAuthor()
                        && !receivedMessage.getAuthor().getId().equals(LoginRepository.getInstance().getLoggedInUser().getUserId())) {
                    List<Message> newMessagesList = new ArrayList(messagesList.getValue());
                    newMessagesList.add(receivedMessage);
                    messagesList.postValue(newMessagesList);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        };

        mSocket.on("message", chatMessagesListener);
    }

    public void removeChatMessagesListener() {
        Log.i("WebSocket", "Remove chat new messages listener");
        mSocket.off("messages", chatMessagesListener);
        mSocket.disconnect();
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

                messagesList.postValue(newMessagesList);
            }
        });

        sendMessage.start();
    }
}