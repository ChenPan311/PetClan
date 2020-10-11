package com.example.android2project.viewmodel;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.android2project.model.Conversation;
import com.example.android2project.model.User;
import com.example.android2project.repository.AuthRepository;
import com.example.android2project.repository.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ChatsViewModel extends ViewModel {
    private static ChatsViewModel chatsViewModel;
    private AuthRepository mAuth;
    private Repository mRepository;

    private List<User> mAllUsers = new ArrayList<>();
    private List<User> mActiveUsers = new ArrayList<>();
    private Map<String, User> mUsersMap = new HashMap<>();
    private List<Conversation> mConversations = new ArrayList<>();

    private MutableLiveData<List<User>> mUsersLiveData;

    private MutableLiveData<List<Conversation>> mDownloadActiveConversationsSucceed;
    private MutableLiveData<String> mDownloadActiveConversationsFailed;

    private final String TAG = "ChatsViewModel";

    public static ChatsViewModel getInstance(Context context) {
        if (chatsViewModel == null) {
            chatsViewModel = new ChatsViewModel(context);
        }
        return chatsViewModel;
    }

    private ChatsViewModel(final Context context) {
        this.mAuth = AuthRepository.getInstance(context);
        this.mRepository = Repository.getInstance(context);
    }

    public MutableLiveData<List<User>> getUsersLiveData() {
        if (mUsersLiveData == null) {
            mUsersLiveData = new MutableLiveData<>();
        }
        return mUsersLiveData;
    }

    public MutableLiveData<List<Conversation>> getDownloadActiveConversationsSucceed() {
        if (mDownloadActiveConversationsSucceed == null) {
            mDownloadActiveConversationsSucceed = new MutableLiveData<>();
            attachSetDownloadActiveConversationsListener();
        }
        return mDownloadActiveConversationsSucceed;
    }

    public MutableLiveData<String> getDownloadActiveConversationsFailed() {
        if (mDownloadActiveConversationsFailed == null) {
            mDownloadActiveConversationsFailed = new MutableLiveData<>();
            attachSetDownloadActiveConversationsListener();
        }
        return mDownloadActiveConversationsFailed;
    }

    private void attachSetDownloadActiveConversationsListener() {
        mRepository.setDownloadActiveChatsListener(new Repository.RepositoryDownloadActiveChatsInterface() {
            @Override
            public void onDownloadActiveChatsSucceed(List<Conversation> conversations) {
                if (!mConversations.isEmpty()) {
                    mConversations.clear();
                }
                mConversations.addAll(conversations);
                Collections.sort(mConversations);
                getRelevantUsers();

                mDownloadActiveConversationsSucceed.setValue(conversations);
            }

            @Override
            public void onDownloadActiveChatsFailed(String error) {
                mDownloadActiveConversationsFailed.setValue(error);
            }
        });
    }


    public List<User> getActiveUsers() {
        return mActiveUsers;
    }

    public void setActiveUsers(List<User> allUsers) {
        if (!mAllUsers.isEmpty()) {
            mAllUsers.clear();
        }

        mAllUsers.addAll(allUsers);
        mUsersLiveData.setValue(getRelevantUsers());
    }

    private List<User> getRelevantUsers() {
        final String myEmail = mAuth.getUserEmail();
        final List<User> relevantUsers = new ArrayList<>();

        if (!mUsersMap.isEmpty()) {
            mUsersMap.clear();
        }

        for (User user : mAllUsers) {
            mUsersMap.put(user.getEmail(), user);
        }

        for (Conversation conversation : mConversations) {
            if (mUsersMap.containsKey(conversation.getRecipientEmail()) && !Objects.requireNonNull(mUsersMap.get(conversation.getRecipientEmail())).getEmail().equals(myEmail)) {
                relevantUsers.add((User) mUsersMap.get(conversation.getRecipientEmail()));
            } else if (mUsersMap.containsKey(conversation.getSenderEmail()) && !Objects.requireNonNull(mUsersMap.get(conversation.getSenderEmail())).getEmail().equals(myEmail)) {
                relevantUsers.add((User) mUsersMap.get(conversation.getSenderEmail()));
            }

            //Old Version: O(n^2)
            /*for (User user : mAllUsers) {
                if ((conversation.getRecipientEmail().equals(user.getEmail()) && !myEmail.equals(user.getEmail())) ||
                        (conversation.getSenderEmail().equals(user.getEmail()) && !myEmail.equals(user.getEmail()))) {
                    relevantUsers.add(user);
                }
            }*/
        }

        if (!this.mActiveUsers.isEmpty()) {
            this.mActiveUsers.clear();
        }
        this.mActiveUsers.addAll(relevantUsers);

        return relevantUsers;
    }

    public List<Conversation> getConversations() {
        return mConversations;
    }

    public void getActiveChats() {
        mRepository.downloadActiveChats();
    }
}
