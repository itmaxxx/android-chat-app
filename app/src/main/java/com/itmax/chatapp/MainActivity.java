package com.itmax.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.core.app.NotificationCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.appcompat.app.AppCompatActivity;

import com.itmax.chatapp.data.model.LoggedInUser;
import com.itmax.chatapp.data.model.Message;
import com.itmax.chatapp.data.model.Notification;
import com.itmax.chatapp.data.model.User;
import com.itmax.chatapp.data.repositories.LoginRepository;
import com.itmax.chatapp.data.repositories.NotificationsRepository;
import com.itmax.chatapp.databinding.ActivityMainBinding;
import com.itmax.chatapp.ui.login.LoginActivity;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private Socket mSocket;
    {
        try {
            mSocket = IO.socket(AppConfig.WS_URL);
        } catch (URISyntaxException e) {
            Log.e("WebSocket", e.getMessage());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create NotificationsRepository instance
        NotificationsRepository.createInstance(this);

        // Connect to web socket server, so that we cache the connection
        mSocket.connect();

        // Listen for new messages
        this.listenForNewMessages();

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        if (binding.appBarMain.fab != null) {
            binding.appBarMain.fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show());
        }
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
        assert navHostFragment != null;
        NavController navController = navHostFragment.getNavController();

        NavigationView navigationView = binding.navView;
        if (navigationView != null) {
            mAppBarConfiguration = new AppBarConfiguration.Builder(
                    // If you want fragment to have navigate back in header, remove it from here
                    R.id.nav_chats, R.id.nav_slideshow, R.id.nav_settings)
                    .setOpenableLayout(binding.drawerLayout)
                    .build();
            NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
            NavigationUI.setupWithNavController(navigationView, navController);
        }

        // Set left navigation user info
        LoggedInUser loggedInUser = LoginRepository.getInstance().getLoggedInUser();
        this.updateNavHeaderUserData(
                loggedInUser.getUser(),
                navigationView.getHeaderView(0).findViewById(R.id.text_view_logged_in_user_fullname),
                navigationView.getHeaderView(0).findViewById(R.id.text_view_logged_in_user_username),
                navigationView.getHeaderView(0).findViewById(R.id.image_view_logged_in_user_image)
        );

    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void updateNavHeaderUserData(User user, TextView fullname, TextView username, ImageView image) {
        fullname.setText(user.getFullname());
        username.setText("@" + user.getUsername());
        Picasso.with(getApplicationContext())
                .load(user.getImage())
                .fit()
                .centerCrop()
                .placeholder(R.drawable.avatar_1)
                .into(image);
    }

    public void handleLogout() {
        Intent loginActivity = new Intent(this, LoginActivity.class);
        startActivity(loginActivity);

        finish();
    }

    public void listenForNewMessages() {
        mSocket.on("message", args -> {
            try {
                String receivedData = args[0].toString();
                Log.i("WebSocket", "Got message in main thread: " + receivedData);

                JSONObject jsonData = new JSONObject(receivedData);

                Message receivedMessage = new Message(jsonData);

                // TODO: Check if new message belongs to current (opened) chat
                //       and if true, don't create notification
                //       Also, check if current user is not message author

                // Create message notification
                Notification notification = new Notification(
                        receivedMessage.getCreatedAt(),
                        receivedMessage.getAuthor().getFullname(),
                        receivedMessage.getText(),
                        NotificationCompat.PRIORITY_DEFAULT,
                        "com.max.chatapp." + receivedMessage.getChatId()
                );
                NotificationsRepository.getInstance().showNotification(notification);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }
}