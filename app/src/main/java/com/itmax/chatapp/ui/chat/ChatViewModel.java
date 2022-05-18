package com.itmax.chatapp.ui.chat;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.itmax.chatapp.data.Result;
import com.itmax.chatapp.data.model.Chat;
import com.itmax.chatapp.data.model.Message;
import com.itmax.chatapp.data.repositories.ChatRepository;

import java.util.ArrayList;
import java.util.List;

public class ChatViewModel extends ViewModel {

    private final ChatRepository chatRepository;
    private final MutableLiveData<List<Message>> messagesList;
    private final MutableLiveData<Chat> chatInfo;

    public ChatViewModel(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
        messagesList = new MutableLiveData<>();
        chatInfo = new MutableLiveData<>();
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
        // Without lambda it doesn't work
        // Runnable also doesn't work
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