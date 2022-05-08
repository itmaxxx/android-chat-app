package com.itmax.chatapp.ui.chats;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.itmax.chatapp.data.model.Chat;
import com.itmax.chatapp.data.repositories.ChatsRepository;

import java.util.List;

public class ChatsViewModel extends ViewModel {

    private final ChatsRepository chatsRepository;
    private final MutableLiveData<List<Chat>> chatsList;

    public ChatsViewModel(ChatsRepository chatsRepository) {
        this.chatsRepository = chatsRepository;
        chatsList = new MutableLiveData<>();
//        List<String> texts = new ArrayList<>();
//        for (int i = 1; i <= 16; i++) {
//            texts.add("This is item # " + i);
//        }
//        chatsList.setValue(texts);
        loadChats();
    }

    private void loadChats() {
        new ChatsThread(chatsRepository, chatsList).start();
    }

    public LiveData<List<Chat>> getChats() {
        return chatsList;
    }
}