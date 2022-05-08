package com.itmax.chatapp.ui.chats;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.itmax.chatapp.data.data_sources.ChatsDataSource;
import com.itmax.chatapp.data.repositories.ChatsRepository;

public class ChatsViewModelFactory implements ViewModelProvider.Factory {

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ChatsViewModel.class)) {
            return (T) new ChatsViewModel(new ChatsRepository(new ChatsDataSource()));
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }

}
