package com.itmax.chatapp.ui.login;

import androidx.lifecycle.MutableLiveData;

import com.itmax.chatapp.R;
import com.itmax.chatapp.data.LoginRepository;
import com.itmax.chatapp.data.Result;
import com.itmax.chatapp.data.model.LoggedInUser;

public class LoginThread extends Thread {
    private String username;
    private String password;
    private LoginRepository loginRepository;
    private MutableLiveData<LoginResult> loginResult;

    LoginThread(String username, String password, LoginRepository loginRepository, MutableLiveData<LoginResult> loginResult) {
        this.username = username;
        this.password = password;
        this.loginRepository = loginRepository;
        this.loginResult = loginResult;
    }

    @Override
    public void run() {

        Result<LoggedInUser> result = this.loginRepository.login(username, password);

        if (result instanceof Result.Success) {
            LoggedInUser data = ((Result.Success<LoggedInUser>) result).getData();
            this.loginResult.postValue(new LoginResult(new LoggedInUserView(data.getDisplayName())));
        } else {
            this.loginResult.postValue(new LoginResult(R.string.login_failed));
        }

    }
}
