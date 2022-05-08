package com.itmax.chatapp.ui.chats;

import androidx.lifecycle.MutableLiveData;

import com.itmax.chatapp.data.Result;
import com.itmax.chatapp.data.model.Chat;
import com.itmax.chatapp.data.repositories.ChatsRepository;
import com.itmax.chatapp.data.repositories.LoginRepository;

import java.util.List;

public class ChatsThread extends Thread {
    private final String token;
    private ChatsRepository chatsRepository;
    private MutableLiveData<List<Chat>> chatsList;

    ChatsThread(ChatsRepository chatsRepository, MutableLiveData<List<Chat>> chatsList) {
        this.chatsRepository = chatsRepository;
        this.chatsList = chatsList;
        this.token = LoginRepository.getInstance().getLoggedInUser().getToken();
    }

    @Override
    public void run() {

        Result<List<Chat>> result = this.chatsRepository.getChats(token);

        if (result instanceof Result.Success) {
            List<Chat> data = ((Result.Success<List<Chat>>) result).getData();
            this.chatsList.postValue(data);
        }
        // TODO: Handle errors, we need something like GetChatsResult

    }
}
