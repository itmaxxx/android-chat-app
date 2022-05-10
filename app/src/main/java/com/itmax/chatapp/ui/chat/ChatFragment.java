package com.itmax.chatapp.ui.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.itmax.chatapp.R;
import com.itmax.chatapp.databinding.FragmentChatBinding;

public class ChatFragment extends Fragment {

    private FragmentChatBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ChatViewModel chatViewModel =
                new ViewModelProvider(this).get(ChatViewModel.class);

        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.hide();

        binding = FragmentChatBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textChat;
        chatViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        String msg = "Opened chat " + getArguments().getString("chatId");
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.show();
    }
}