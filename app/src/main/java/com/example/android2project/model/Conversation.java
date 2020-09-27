package com.example.android2project.model;

public class Conversation implements Comparable<Object> {
    private String mChatId;
    private String mSenderEmail;
    private String mRecipientEmail;
    private ChatMessage mLastMessage;

    public Conversation() {}

    public Conversation(String senderEmail, String recipientEmail,ChatMessage lastMessage) {
        final String id1 = senderEmail.replace(".", "");
        final String id2 = recipientEmail.replace(".", "");

        this.mChatId = id1 + "&" + id2;
        if (id2.compareTo(id1) < 0) {
            this.mChatId = id2 + "&" + id1;
        }

        this.mSenderEmail = senderEmail;
        this.mRecipientEmail = recipientEmail;
        this.mLastMessage=lastMessage;
    }

    public ChatMessage getLastMessage() {
        return mLastMessage;
    }

    public void setLastMessage(ChatMessage LastMessage) {
        this.mLastMessage = LastMessage;
    }

    public String getChatId() {
        return mChatId;
    }

    public void setChatId(String chatId) {
        this.mChatId = chatId;
    }

    public String getSenderEmail() {
        return mSenderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.mSenderEmail = senderEmail;
    }

    public String getRecipientEmail() {
        return mRecipientEmail;
    }

    public void setRecipientEmail(String recipientEmail) {
        this.mRecipientEmail = recipientEmail;
    }

    @Override
    public String toString() {
        return "Conversation{" +
                "mChatId='" + mChatId + '\'' +
                ", mSenderEmail='" + mSenderEmail + '\'' +
                ", mRecipientEmail='" + mRecipientEmail + '\'' +
                ", mLastMessage=" + mLastMessage +
                '}';
    }

    @Override
    public int compareTo(Object object) {
        if (object instanceof Conversation) {
            Conversation otherConversation = (Conversation) object;
            return otherConversation.getLastMessage().getTime().compareTo(this.getLastMessage().getTime());
        }
        return 0;
    }
}
