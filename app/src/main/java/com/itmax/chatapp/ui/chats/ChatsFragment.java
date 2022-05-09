package com.itmax.chatapp.ui.chats;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.itmax.chatapp.R;
import com.itmax.chatapp.data.model.Chat;
import com.itmax.chatapp.databinding.FragmentChatsBinding;
import com.itmax.chatapp.databinding.ItemChatBinding;

/**
 * Fragment that demonstrates a responsive layout pattern where the format of the content
 * transforms depending on the size of the screen. Specifically this Fragment shows items in
 * the [RecyclerView] using LinearLayoutManager in a small screen
 * and shows items using GridLayoutManager in a large screen.
 */
public class ChatsFragment extends Fragment {

    private FragmentChatsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ChatsViewModel chatsViewModel = new ViewModelProvider(this, new ChatsViewModelFactory())
                .get(ChatsViewModel.class);

        binding = FragmentChatsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        RecyclerView recyclerView = binding.recyclerviewChats;

        ListAdapter<Chat, ChatsViewHolder> adapter = new ChatsAdapter();
        recyclerView.setAdapter(adapter);

        // Listen to chats list changes and update recycle view
        chatsViewModel.getChats().observe(getViewLifecycleOwner(), adapter::submitList);

        // Handle refresh chats list
        SwipeRefreshLayout swipeContainer = binding.chatsSwipeContainer;
        swipeContainer.setOnRefreshListener(() -> {
            Log.i("Chats", "Refresh chats list");
            chatsViewModel.loadChats();
            swipeContainer.setRefreshing(false);
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private static class ChatsAdapter extends ListAdapter<Chat, ChatsViewHolder> {

        protected ChatsAdapter() {
            super(new DiffUtil.ItemCallback<Chat>() {
                @Override
                public boolean areItemsTheSame(@NonNull Chat oldItem, @NonNull Chat newItem) {
                    return oldItem.getName().equals(newItem.getName());
                }

                @Override
                public boolean areContentsTheSame(@NonNull Chat oldItem, @NonNull Chat newItem) {
                    return oldItem.getName().equals(newItem.getName());
                }
            });
        }

        @NonNull
        @Override
        public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemChatBinding binding = ItemChatBinding.inflate(LayoutInflater.from(parent.getContext()));
            return new ChatsViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull ChatsViewHolder holder, int position) {
            holder.textView.setText(this.getItem(position).getName());
            holder.imageView.setImageDrawable(
                    ResourcesCompat.getDrawable(holder.imageView.getResources(),
                            R.drawable.avatar_1,
                            null));
        }
    }

    private static class ChatsViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imageView;
        private final TextView textView;

        public ChatsViewHolder(ItemChatBinding binding) {
            super(binding.getRoot());
            imageView = binding.imageViewItemChat;
            textView = binding.textViewItemChat;
        }
    }
}