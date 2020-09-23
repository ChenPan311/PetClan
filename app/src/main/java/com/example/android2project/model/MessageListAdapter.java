package com.example.android2project.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.android2project.R;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MessageListAdapter extends RecyclerView.Adapter {
    private static final int TYPE_MESSAGE_SENT = 1;
    private static final int TYPE_MESSAGE_RECEIVED = 2;

    private Context mContext;
    private List<ChatMessage> mMessageList;
    private User mCurrentUser;

    public MessageListAdapter(Context context, List<ChatMessage> messageList,User currentUser) {
        mContext = context;
        mMessageList = messageList;
        mCurrentUser = currentUser;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_sent, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = (ChatMessage) mMessageList.get(position);

        switch (holder.getItemViewType()) {
            case TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage message = (ChatMessage) mMessageList.get(position);

        if (message.getSender().getEmail().equals(mCurrentUser.getEmail())) {
            // If the current user is the sender of the message
            return TYPE_MESSAGE_SENT;
        } else {
            // If some other user sent the message
            return TYPE_MESSAGE_RECEIVED;
        }
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, nameText;
        ImageView profileImage;

        public ReceivedMessageHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.message_body_tv);
            timeText = itemView.findViewById(R.id.message_time_tv);
            nameText = itemView.findViewById(R.id.message_name_tv);
            profileImage = itemView.findViewById(R.id.message_profile_pic_iv);
        }

        void bind(ChatMessage message) {
            messageText.setText(message.getContent());
            timeText.setText(timestampToTimeAgo(message.getTime()));
            nameText.setText(String.format("%s %s", message.getSender().getFirstName(), message.getSender().getLastName()));
            Glide.with(mContext).load(message.getSender().getPhotoUri()).into(profileImage);
        }
    }


    private class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText;

        public SentMessageHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.message_body_tv);
            timeText = itemView.findViewById(R.id.message_time_tv);
        }

        void bind(ChatMessage message) {
            messageText.setText(message.getContent());
            timeText.setText(timestampToTimeAgo(message.getTime()));
        }
    }

    private String timestampToTimeAgo(Date date) {
        String language = Locale.getDefault().getLanguage();
        PrettyTime prettyTime = new PrettyTime(new Locale(language));
        return prettyTime.format(date);
    }
}