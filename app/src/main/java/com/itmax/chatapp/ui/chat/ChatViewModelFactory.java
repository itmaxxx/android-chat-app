package com.itmax.chatapp.ui.chat;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.itmax.chatapp.data.data_sources.ChatDataSource;
import com.itmax.chatapp.data.repositories.ChatRepository;

public class ChatViewModelFactory implements ViewModelProvider.Factory {

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ChatViewModel.class)) {
            return (T) new ChatViewModel(new ChatRepository(new ChatDataSource()));
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }

}
