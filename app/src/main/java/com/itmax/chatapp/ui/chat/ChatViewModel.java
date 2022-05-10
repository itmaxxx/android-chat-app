package com.itmax.chatapp.ui.chat;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ChatViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public ChatViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is chat fragment from code");
    }

    public LiveData<String> getText() {
        return mText;
    }
}