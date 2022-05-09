package com.itmax.chatapp.data.data_sources;

import android.util.Log;

import com.itmax.chatapp.AppConfig;
import com.itmax.chatapp.data.Result;
import com.itmax.chatapp.data.model.Chat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatsDataSource {

    private final OkHttpClient client = new OkHttpClient();
    private static final MediaType JSON = MediaType.get("application/json; charaset=utf-8");

    public Result<List<Chat>> getChats(String token) {

        try {
            Log.i("Chats.getChats", token);

            Request request = new Request.Builder()
                    .url(AppConfig.API_URL + "/chats")
                    .addHeader("Authorization", "Bearer " + token)
                    .get()
                    .build();

            Response response = client.newCall(request).execute();

            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            String textResponse = response.body().string();

            if (textResponse.isEmpty()) {
                throw new Exception("Failed to read response body");
            }

            JSONObject jsonResponse = new JSONObject(textResponse);

            // TODO: Check if we get exactly type: ChatsList
            if (jsonResponse.has("error") && jsonResponse.getBoolean("error")) {
                return new Result.Error(new IOException("Error getting chats list"));
            }

            Log.i("Chats", jsonResponse.getJSONArray("data").toString());

            JSONArray data = jsonResponse.getJSONArray("data");

            List<Chat> chatList = new ArrayList<Chat>();

            for (int i = 0; i < data.length(); i++) {
                JSONObject chat = data.getJSONObject(i);

                chatList.add(new Chat(
                        chat.getString("id"),
                        chat.getString("name"),
                        chat.getString("image"),
                        chat.getString("lastMessage")
                ));
            }

            return new Result.Success(chatList);
        }
        catch (Exception e) {
            Log.e("Chats.ChatsDataSource", e.getMessage());

            return new Result.Error(new IOException("Error getting chats list", e));
        }

    }

}
