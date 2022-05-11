package com.itmax.chatapp.data.data_sources;

import android.util.Log;

import com.itmax.chatapp.AppConfig;
import com.itmax.chatapp.data.Result;
import com.itmax.chatapp.data.model.Message;
import com.itmax.chatapp.data.model.User;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ChatDataSource {

    private final OkHttpClient client = new OkHttpClient();
    private static final MediaType JSON = MediaType.get("application/json; charaset=utf-8");

    public Result<List<Message>> getChatMessages(String chatId, String token) {
        try {
            Log.i("Chat.getChatMessages", "chatId: " + chatId);

            Request request = new Request.Builder()
                    .url(AppConfig.API_URL + "/chats/" + chatId + "/messages")
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

            // TODO: Check if we get exactly type: ChatMessagesList
            if (jsonResponse.has("error") && jsonResponse.getBoolean("error")) {
                return new Result.Error(new IOException("Error getting chat messages"));
            }

            Log.i("Chat.getChatMessages", jsonResponse.getJSONArray("data").toString());

            JSONArray data = jsonResponse.getJSONArray("data");

            List<Message> messagesList = new ArrayList<>();

            for (int i = 0; i < data.length(); i++) {
                JSONObject message = data.getJSONObject(i);
                JSONObject author = message.getJSONObject("author");

                messagesList.add(
                        new Message(
                                message.getString("id"),
                                message.getString("chatId"),
                                message.getString("text"),
                                new User(
                                        author.getString("id"),
                                        author.getString("fullname"),
                                        author.getString("username"),
                                        author.getString("image")
                                ),
                                message.getInt("createdAt"),
                                message.getInt("updatedAt")
                        )
                );
            }

            return new Result.Success(messagesList);
        }
        catch (Exception e) {
            Log.e("Chat.ChatDataSource.getChatMessages", e.getMessage());

            return new Result.Error(new IOException("Error getting chat messages", e));
        }
    }
}
