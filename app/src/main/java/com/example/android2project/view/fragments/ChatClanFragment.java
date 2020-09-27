package com.example.android2project.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android2project.R;
import com.example.android2project.model.ChatClanAdapter;
import com.example.android2project.model.User;
import com.example.android2project.model.ViewModelEnum;
import com.example.android2project.viewmodel.ChatClanViewModel;
import com.example.android2project.viewmodel.ViewModelFactory;

<<<<<<< HEAD
=======
import java.util.ArrayList;
>>>>>>> bf66adb70a3c63a9a94e97680925c2220bbdd029
import java.util.List;

public class ChatClanFragment extends Fragment {
    private ChatClanViewModel mViewModel;
    private RecyclerView mRecyclerview;
    private ChatClanAdapter mAdapter;
<<<<<<< HEAD

=======
    private ChatClanViewModel mViewModel;
>>>>>>> bf66adb70a3c63a9a94e97680925c2220bbdd029
    private Observer<List<User>> usersObserver;

    public static ChatClanFragment newInstance() {
        return new ChatClanFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this, new ViewModelFactory(getContext(),
                ViewModelEnum.ChatClan)).get(ChatClanViewModel.class);
<<<<<<< HEAD
=======
//        mViewModel.getAllUsers();
>>>>>>> bf66adb70a3c63a9a94e97680925c2220bbdd029

        usersObserver = new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
<<<<<<< HEAD
                mAdapter = new ChatClanAdapter(getContext(), users);
=======
                mAdapter = new ChatClanAdapter(getContext(),users);
                mRecyclerview.setAdapter(mAdapter);
>>>>>>> bf66adb70a3c63a9a94e97680925c2220bbdd029
                mAdapter.setFriendItemListener(new ChatClanAdapter.FriendItemListener() {
                    @Override
                    public void onClicked(int position, View view) {
                        User recipient = mViewModel.getUsers().get(position);
                        ConversationFragment.newInstance(recipient)
                                .show(getParentFragmentManager()
                                        .beginTransaction(), "conversation_fragment");
                    }
                });
                mRecyclerview.setAdapter(mAdapter);
            }
        };

<<<<<<< HEAD
        mViewModel.getUsersLiveData().observe(this, usersObserver);
=======
        mViewModel.getUsersLiveData().observe(this,usersObserver);
>>>>>>> bf66adb70a3c63a9a94e97680925c2220bbdd029
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.chat_clan_fragment, container, false);
        mRecyclerview = rootView.findViewById(R.id.chatclan_recyclerview);
        mRecyclerview.setHasFixedSize(true);
        mRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));

        return rootView;
    }
<<<<<<< HEAD
}
=======

    @Override
    public void onStop() {
        mViewModel.getUsersLiveData().removeObservers(this);
        super.onStop();
    }
}
>>>>>>> bf66adb70a3c63a9a94e97680925c2220bbdd029
