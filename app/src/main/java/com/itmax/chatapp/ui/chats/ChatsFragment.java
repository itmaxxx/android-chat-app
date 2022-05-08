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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.itmax.chatapp.R;
import com.itmax.chatapp.data.data_sources.ChatsDataSource;
import com.itmax.chatapp.data.model.Chat;
import com.itmax.chatapp.data.repositories.ChatsRepository;
import com.itmax.chatapp.databinding.FragmentChatsBinding;
import com.itmax.chatapp.databinding.ItemChatBinding;
import com.itmax.chatapp.ui.login.LoginViewModel;
import com.itmax.chatapp.ui.login.LoginViewModelFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

//        List<Chat> tempList = new ArrayList<>();
//        tempList.add(new Chat("Hello world", "img_url", "Hi there!"));

        // FIXME: Not working
        ListAdapter<Chat, ChatsViewHolder> adapter = new ChatsAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        // TODO: Handle chats list update here
        chatsViewModel.getChats().observe(getViewLifecycleOwner(), adapter::submitList);
//        chatsViewModel.getChats().observe(getViewLifecycleOwner(), list -> {
//            Log.i("Chats", "Chats changed, need to handle onChange observer" + list.toString());
//
//            adapter.submitList(list);
//        });
//        chatsViewModel.getChats().observe(getViewLifecycleOwner(), new Observer<List<Chat>>() {
//            @Override
//            public void onChanged(List<Chat> chats) {
//                Log.i("Chats", "Chats changed, need to handle onChange observer" + chats.toString());
//
//                adapter::submitList(chats);
////                ListAdapter<Chat, ChatsViewHolder> adapter = new ChatsAdapter(chats);
////                recyclerView.setAdapter(adapter);
//            }
//        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private static class ChatsAdapter extends ListAdapter<Chat, ChatsViewHolder> {

        private final List<Chat> chatsList;

        protected ChatsAdapter(List<Chat> chatsList) {
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

            Log.i("ChatsArr", chatsList.toString());

            this.chatsList = new ArrayList<>(chatsList);
        }

        @NonNull
        @Override
        public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Log.i("Chats", "Create view holder" + chatsList.toString());
            ItemChatBinding binding = ItemChatBinding.inflate(LayoutInflater.from(parent.getContext()));
            return new ChatsViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull ChatsViewHolder holder, int position) {
            Log.i("Chats", "Bind view holder" + chatsList.toString());
            holder.textView.setText(chatsList.get(position).getName());
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