package com.itmax.chatapp.ui.chats;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.itmax.chatapp.R;
import com.itmax.chatapp.data.model.Chat;
import com.itmax.chatapp.data.model.Message;
import com.itmax.chatapp.databinding.FragmentChatsBinding;
import com.itmax.chatapp.databinding.ItemChatBinding;
import com.itmax.chatapp.ui.create_chat_dialog.CreateChatDialogFragment;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.List;

public class ChatsFragment extends Fragment implements CreateChatDialogFragment.CreateChatDialogListener {

    private FragmentChatsBinding binding;
    private ChatsViewModel chatsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        chatsViewModel = new ViewModelProvider(this, new ChatsViewModelFactory())
                .get(ChatsViewModel.class);

        binding = FragmentChatsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        RecyclerView recyclerView = binding.recyclerviewChats;

        ListAdapter<Chat, ChatsViewHolder> adapter = new ChatsAdapter(getContext());
        recyclerView.setAdapter(adapter);

        // Listen to chats list changes and update recycle view
        chatsViewModel.getFilteredChats().observe(getViewLifecycleOwner(), adapter::submitList);

        // Listen to websocket server chat invitations
        chatsViewModel.listenForChatsInvitations();

        // Handle refresh chats list
        SwipeRefreshLayout swipeContainer = binding.chatsSwipeContainer;
        swipeContainer.setOnRefreshListener(() -> {
            Log.i("Chats", "Refresh chats list");
            chatsViewModel.loadChats();
            swipeContainer.setRefreshing(false);
        });

        // Create chat dialog
        CreateChatDialogFragment createChatDialogFragment = new CreateChatDialogFragment();
        createChatDialogFragment.setTargetFragment(this, 100);

        // Hang create chat dialog on fab click
        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            createChatDialogFragment.show(this.getParentFragmentManager(), "create_chat_dialog_tag");
        });

        // Create search option menu
        setHasOptionsMenu(true);

        // Update filtered chats list when we got update in original chats list
        chatsViewModel.getChats().observe(getViewLifecycleOwner(), v -> {
            chatsViewModel.updateFilteredChatsList();
        });

        return root;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Type here to search");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                chatsViewModel.filterChatsByName(s);
                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        TextView chatName = dialog.getDialog().findViewById(R.id.dialog_create_chat_name);
        Log.i("ChatsFragment", "Create chat " + chatName.getText());
        chatsViewModel.createChat(chatName.getText().toString());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private static class ChatsAdapter extends ListAdapter<Chat, ChatsViewHolder> {

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

        protected ChatsAdapter(Context context) {
            super(new DiffUtil.ItemCallback<Chat>() {
                @Override
                public boolean areItemsTheSame(@NonNull Chat oldItem, @NonNull Chat newItem) {
                    return oldItem.getId().equals(newItem.getId());
                }

                @Override
                public boolean areContentsTheSame(@NonNull Chat oldItem, @NonNull Chat newItem) {
                    return oldItem.getName().equals(newItem.getName())
                            && oldItem.getImage().equals(newItem.getImage());
                }
            });

            this.context = context;
        }

        @NonNull
        @Override
        public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemChatBinding binding = ItemChatBinding.inflate(LayoutInflater.from(parent.getContext()));
            return new ChatsViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull ChatsViewHolder holder, int position) {
            Message lastChatMessage = this.getItem(position).getLastMessage();

            holder.name.setText(this.getItem(position).getName());
            String lastMessageText = "<b>" + lastChatMessage.getAuthor().getFullname() + "</b>: " + lastChatMessage.getText();
            holder.lastMessage.setText(HtmlCompat.fromHtml(lastMessageText, HtmlCompat.FROM_HTML_MODE_LEGACY));
            Picasso.with(context)
                    .load(this.getItem(position).getImage())
                    .fit()
                    .centerCrop()
                    .placeholder(avatars.get(position % avatars.size()))
                    .into(holder.image);

            holder.itemView.setOnClickListener(v -> {
                // Handle navigate to chat screen
                Bundle bundle = new Bundle();
                bundle.putString("chatId", this.getItem(position).getId());
                Navigation.findNavController(v).navigate(R.id.nav_chat, bundle);
            });
        }
    }

    private static class ChatsViewHolder extends RecyclerView.ViewHolder {

        private final ImageView image;
        private final TextView name;
        private final TextView lastMessage;

        public ChatsViewHolder(ItemChatBinding binding) {
            super(binding.getRoot());
            image = binding.imageViewItemChatImage;
            name = binding.textViewItemChatName;
            lastMessage = binding.textViewItemChatLastMessage;
        }
    }
}