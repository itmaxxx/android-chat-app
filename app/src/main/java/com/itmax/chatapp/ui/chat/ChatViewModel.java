package com.itmax.chatapp.ui.chat;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.itmax.chatapp.data.Result;
import com.itmax.chatapp.data.model.Message;
import com.itmax.chatapp.data.repositories.ChatRepository;

import java.util.List;

public class ChatViewModel extends ViewModel {

    private final ChatRepository chatRepository;
    private final MutableLiveData<List<Message>> messagesList;

    public ChatViewModel(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
        messagesList = new MutableLiveData<>();
    }

    public void loadChatMessages(String chatId) {
        // Without lamba it doesn't work
        // Runnable also doesn't work
        Thread loadChats = new Thread(() -> {
            Result<List<Message>> result = chatRepository.getChatMessages(chatId);

            if (result instanceof Result.Success) {
                List<Message> data = ((Result.Success<List<Message>>) result).getData();
                messagesList.postValue(data);
            }
        });

        loadChats.start();
    }

    public LiveData<List<Message>> getChatMessages() {
        return this.messagesList;
    }
}