package com.itmax.chatapp.data.data_sources;

import android.util.Log;

import com.itmax.chatapp.AppConfig;
import com.itmax.chatapp.data.Result;
import com.itmax.chatapp.data.model.LoggedInUser;
import com.itmax.chatapp.data.model.User;

import org.json.JSONObject;

import java.io.IOException;

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

            RequestBody body = RequestBody.create(JSON, "{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}");
            Request request = new Request.Builder()
                    .url(AppConfig.API_URL + "/auth/sign-in")
                    .post(body)
                    .build();

            Response response = client.newCall(request).execute();

            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            String textResponse = response.body().string();

            if (textResponse.isEmpty()) {
                throw new Exception("Failed to read response body");
            }

            JSONObject jsonResponse = new JSONObject(textResponse);

            if (jsonResponse.has("error") && jsonResponse.getBoolean("error")
                || !jsonResponse.has("data")) {
                return new Result.Error(new IOException("Error logging in"));
            }

            JSONObject userJsonObject = jsonResponse.getJSONObject("data");
            User user = new User(userJsonObject);

            return new Result.Success(new LoggedInUser(
                    user,
                    jsonResponse.getString("token")
            ));
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