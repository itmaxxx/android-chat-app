package com.itmax.chatapp.ui.chats;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.itmax.chatapp.AppConfig;
import com.itmax.chatapp.data.model.Chat;
import com.itmax.chatapp.data.model.Message;
import com.itmax.chatapp.data.repositories.ChatsRepository;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;

public class ChatsViewModel extends ViewModel {

    private final ChatsRepository chatsRepository;
    private final MutableLiveData<List<Chat>> chatsList;
    private Socket mSocket;
    {
        try {
            mSocket = IO.socket(AppConfig.WS_URL);
        } catch (URISyntaxException e) {
            Log.e("WebSocket", e.getMessage());
        }
    }

    public ChatsViewModel(ChatsRepository chatsRepository) {
        this.chatsRepository = chatsRepository;
        chatsList = new MutableLiveData<>();
        loadChats();
    }

    public void listenForChatsInvitations() {
        mSocket.connect();

        mSocket.on("chat_invitation", args -> {
            try {
                String receivedData = args[0].toString();
                Log.i("WebSocket", "Got invitation : " + receivedData);

                JSONObject jsonData = new JSONObject(receivedData);

                List<Chat> newChatsList = new ArrayList(chatsList.getValue());
                newChatsList.add(new Chat(jsonData));
                chatsList.postValue(newChatsList);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    public void loadChats() {
        new ChatsThread(chatsRepository, chatsList).start();
    }

    public LiveData<List<Chat>> getChats() {
        return chatsList;
    }
}