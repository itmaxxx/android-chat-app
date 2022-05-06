package com.itmax.chatapp.data;

import android.util.Log;

import com.itmax.chatapp.R;
import com.itmax.chatapp.data.model.LoggedInUser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    private final OkHttpClient client = new OkHttpClient();
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public Result<LoggedInUser> login(String username, String password) {

        try {
            Log.i("SignIn.LoginDataSource.login()", username + ":" + password);

            RequestBody body = RequestBody.create(JSON, "{\"username\":\"itmax\",\"password\":\"qwerty\"}");
            Request request = new Request.Builder()
                    .url("http://192.168.0.100:3000/api/auth/sign-in")
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                Log.i("SignIn.LoginDataSource.login().response", response.body().string());
            }

            return new Result.Success(new LoggedInUser("userid", "displayName"));
        }
        catch (Exception e) {
            Log.e("SignIn.LoginDataSource", e.getMessage());

            return new Result.Error(new IOException("Error logging in", e));
        }

    }

    public void logout() {
        // TODO: revoke authentication
    }
}