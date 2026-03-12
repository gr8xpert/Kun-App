package com.example.kunworld.api;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class GroqResponse {
    @SerializedName("id")
    private String id;

    @SerializedName("choices")
    private List<Choice> choices;

    @SerializedName("error")
    private Error error;

    public String getId() {
        return id;
    }

    public List<Choice> getChoices() {
        return choices;
    }

    public Error getError() {
        return error;
    }

    public String getMessageContent() {
        if (choices != null && !choices.isEmpty()) {
            Choice choice = choices.get(0);
            if (choice.getMessage() != null) {
                return choice.getMessage().getContent();
            }
        }
        return null;
    }

    public static class Choice {
        @SerializedName("index")
        private int index;

        @SerializedName("message")
        private Message message;

        @SerializedName("finish_reason")
        private String finishReason;

        public int getIndex() {
            return index;
        }

        public Message getMessage() {
            return message;
        }

        public String getFinishReason() {
            return finishReason;
        }
    }

    public static class Message {
        @SerializedName("role")
        private String role;

        @SerializedName("content")
        private String content;

        public String getRole() {
            return role;
        }

        public String getContent() {
            return content;
        }
    }

    public static class Error {
        @SerializedName("message")
        private String message;

        @SerializedName("type")
        private String type;

        public String getMessage() {
            return message;
        }

        public String getType() {
            return type;
        }
    }
}
