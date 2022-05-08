package com.itmax.chatapp.data.repositories;

import com.itmax.chatapp.data.Result;
import com.itmax.chatapp.data.data_sources.ChatsDataSource;
import com.itmax.chatapp.data.model.Chat;

import java.util.List;

public class ChatsRepository {

    private ChatsDataSource dataSource;
    private List<Chat> chatList;

    public ChatsRepository(ChatsDataSource dataSource) {
        this.dataSource = dataSource;
    }

    private void setChatsList(List<Chat> chatsList) {
        this.chatList = chatsList;
    }

    public Result<List<Chat>> getChats(String token) {
        Result<List<Chat>> result = dataSource.getChats(token);

        if (result instanceof Result.Success) {
            setChatsList(((Result.Success<List<Chat>>) result).getData());
        }

        return result;
    }
}
