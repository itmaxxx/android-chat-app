package com.itmax.chatapp.data.repositories;

import com.itmax.chatapp.data.Result;
import com.itmax.chatapp.data.data_sources.ChatDataSource;
import com.itmax.chatapp.data.model.Message;

import java.util.List;

public class ChatRepository {

    private ChatDataSource dataSource;
    private String token;

    public ChatRepository(ChatDataSource dataSource) {
        this.dataSource = dataSource;
        this.token = LoginRepository.getInstance().getLoggedInUser().getToken();
    }

    public Result<List<Message>> getChatMessages(String chatId) {
        return dataSource.getChatMessages(chatId, this.token);
    }

}