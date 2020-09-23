package com.example.android2project.model;

import java.util.Date;

public class ChatMessage {
    private String mContent;
    private User mRecipient;
    private Date mTime;

    public ChatMessage() {}

    public ChatMessage(String content, User recipient) {
        this.mContent = content;
        this.mRecipient = recipient;
        this.mTime = new Date();
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        this.mContent = content;
    }

    public User getRecipient() {
        return mRecipient;
    }

    public void setRecipient(User recipient) {
        this.mRecipient = recipient;
    }

    public Date getTime() {
        return mTime;
    }

    public void setTime(Date mTime) {
        this.mTime = mTime;
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "mContent='" + mContent + '\'' +
                ", mRecipient=" + mRecipient +
                ", mTime=" + mTime +
                '}';
    }
}


