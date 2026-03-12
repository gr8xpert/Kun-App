package com.example.kunworld.data.models;

public class ChatMessage {
    public static final int TYPE_USER = 0;
    public static final int TYPE_BOT = 1;
    public static final int TYPE_TYPING = 2;

    private String id;
    private String content;
    private int type;
    private long timestamp;
    private boolean isError;

    public ChatMessage() {
    }

    public ChatMessage(String content, int type) {
        this.id = String.valueOf(System.currentTimeMillis());
        this.content = content;
        this.type = type;
        this.timestamp = System.currentTimeMillis();
        this.isError = false;
    }

    public static ChatMessage createUserMessage(String content) {
        return new ChatMessage(content, TYPE_USER);
    }

    public static ChatMessage createBotMessage(String content) {
        return new ChatMessage(content, TYPE_BOT);
    }

    public static ChatMessage createTypingIndicator() {
        return new ChatMessage("", TYPE_TYPING);
    }

    public static ChatMessage createErrorMessage(String content) {
        ChatMessage message = new ChatMessage(content, TYPE_BOT);
        message.setError(true);
        return message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isError() {
        return isError;
    }

    public void setError(boolean error) {
        isError = error;
    }

    public boolean isUser() {
        return type == TYPE_USER;
    }

    public boolean isBot() {
        return type == TYPE_BOT;
    }

    public boolean isTyping() {
        return type == TYPE_TYPING;
    }
}
