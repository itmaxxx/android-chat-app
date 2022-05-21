package com.itmax.chatapp.ui.chat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.itmax.chatapp.MainActivity;
import com.itmax.chatapp.R;
import com.itmax.chatapp.data.model.Message;
import com.itmax.chatapp.data.model.User;
import com.itmax.chatapp.databinding.FragmentChatBinding;
import com.itmax.chatapp.databinding.ItemMessageBinding;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.Arrays;
import java.util.List;

public class ChatFragment extends Fragment {

    private FragmentChatBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ChatViewModel chatViewModel = new ViewModelProvider(this, new ChatViewModelFactory())
                .get(ChatViewModel.class);

        // Hide floating button
        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.hide();

        binding = FragmentChatBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        RecyclerView recyclerView = binding.recyclerviewChatMessages;

        ListAdapter<Message, MessageViewHolder> adapter = new ChatFragment.MessagesAdapter(getContext());
        recyclerView.setAdapter(adapter);

        // Get chat messages
        chatViewModel.getChatMessages(getArguments().getString("chatId"));

        // Listen to messages list changes and update recycle view
        chatViewModel.getChatMessages().observe(getViewLifecycleOwner(), adapter::submitList);

        // Listen to websocket message event
        chatViewModel.listenForChatMessages(getArguments().getString("chatId"));

        // Show opened chat id
        String msg = "Opened chat " + getArguments().getString("chatId");
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();

        // Get chat info
        chatViewModel.getChatInfo(getArguments().getString("chatId"));
        chatViewModel.getChatInfo().observe(getViewLifecycleOwner(), (chatInfo) -> {
            ((MainActivity) getActivity()).getSupportActionBar().setTitle(chatInfo.getName());
            ((MainActivity) getActivity()).getSupportActionBar().setSubtitle("2 participants");

            Picasso.with(getContext())
                    .load(chatInfo.getImage())
                    .resize(140, 140)
                    .centerCrop()
                    .placeholder(R.drawable.avatar_1)
                    .into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            Drawable loadedChatImage = new BitmapDrawable(getContext().getResources(), bitmap);

                            ((MainActivity) getActivity()).getSupportActionBar().setIcon(loadedChatImage);
                        }

                        @Override
                        public void onBitmapFailed(Drawable errorDrawable) {

                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                        }
                    });
        });

        // Handle message send
        binding.sendMessageButton.setOnClickListener(v -> {
            try {
                final String messageText = binding.messageEditText.getText() + "";

                chatViewModel.sendMessage(getArguments().getString("chatId"), messageText);

                recyclerView.scrollToPosition(adapter.getItemCount() - 1);

                binding.messageEditText.setText("");
            } catch(Exception ex) {
                Log.e("Chat", ex.getMessage());
                Toast.makeText(getContext(), "Failed to send message", Toast.LENGTH_LONG).show();
            }
        });

        // Scroll to bottom when we open chat or screen keyboard
        binding.recyclerviewChatMessages.addOnLayoutChangeListener((view, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            recyclerView.scrollToPosition(adapter.getItemCount() - 1);
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;

        // Show floating button
        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.show();

        // Reset nav subtitle and icon
        ((MainActivity) getActivity()).getSupportActionBar().setSubtitle("");
        ((MainActivity) getActivity()).getSupportActionBar().setIcon(0);
    }

    private static class MessagesAdapter extends ListAdapter<Message, MessageViewHolder> {

        private final List<Integer> avatars = Arrays.asList(
                R.drawable.avatar_1,
                R.drawable.avatar_2,
                R.drawable.avatar_3,
                R.drawable.avatar_4,
                R.drawable.avatar_5,
                R.drawable.avatar_6,
                R.drawable.avatar_7,
                R.drawable.avatar_8,
                R.drawable.avatar_9,
                R.drawable.avatar_10,
                R.drawable.avatar_11,
                R.drawable.avatar_12,
                R.drawable.avatar_13,
                R.drawable.avatar_14,
                R.drawable.avatar_15,
                R.drawable.avatar_16);

        private final Context context;

        protected MessagesAdapter(Context context) {
            super(new DiffUtil.ItemCallback<Message>() {
                @Override
                public boolean areItemsTheSame(@NonNull Message oldItem, @NonNull Message newItem) {
                    return oldItem.getId().equals(newItem.getId());
                }

                @Override
                public boolean areContentsTheSame(@NonNull Message oldItem, @NonNull Message newItem) {
                    return oldItem.getText().equals(newItem.getText());
                }
            });

            this.context = context;
        }

        @NonNull
        @Override
        public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemMessageBinding binding = ItemMessageBinding.inflate(LayoutInflater.from(parent.getContext()));
            return new MessageViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
            Message message = this.getItem(position);
            User author = this.getItem(position).getAuthor();

            if (message.getIsAuthor()) {
                holder.container.setGravity(Gravity.END);
                holder.contentContainer.setBackgroundResource(R.drawable.message_item_drawable_right);
                holder.authorName.setVisibility(View.GONE);
                holder.authorImage.setVisibility(View.GONE);
                holder.text.setTextColor(Color.WHITE);
                holder.time.setTextColor(Color.LTGRAY);
            } else {
                holder.container.setGravity(Gravity.START);
                holder.contentContainer.setBackgroundResource(R.drawable.message_item_drawable_left);
                holder.authorName.setVisibility(View.VISIBLE);
                holder.authorImage.setVisibility(View.VISIBLE);
                holder.text.setTextColor(Color.BLACK);
                holder.time.setTextColor(Color.GRAY);
            }

            holder.authorName.setText(author.getFullname());
            holder.text.setText(message.getText());
            Picasso.with(context)
                    .load(author.getImage())
                    .fit()
                    .centerCrop()
                    .placeholder(avatars.get(position % avatars.size()))
                    .into(holder.authorImage);
        }
    }

    private static class MessageViewHolder extends RecyclerView.ViewHolder {

        private final ImageView authorImage;
        private final TextView authorName;
        private final TextView text;
        private final TextView time;
        private final LinearLayout container;
        private final LinearLayout contentContainer;

        public MessageViewHolder(ItemMessageBinding binding) {
            super(binding.getRoot());
            authorImage = binding.messageItemAuthorImage;
            authorName = binding.messageItemAuthorName;
            text = binding.messageItemContent;
            time = binding.messageItemTime;
            container = binding.messageItemContainer;
            contentContainer = binding.messageItemContentContainer;
        }
    }
}