package com.example.kunworld.ui.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kunworld.R;
import com.example.kunworld.api.GroqChatHelper;
import com.example.kunworld.data.models.ChatMessage;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class AIChatFragment extends Fragment {

    private RecyclerView rvMessages;
    private TextInputEditText etMessage;
    private FloatingActionButton fabSend;
    private HorizontalScrollView scrollSuggestions;
    private Chip chipSuggestion1, chipSuggestion2, chipSuggestion3;
    private Toolbar toolbar;

    private ChatAdapter adapter;
    private List<ChatMessage> messages = new ArrayList<>();
    private GroqChatHelper groqChatHelper;
    private boolean isViewDestroyed = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ai_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isViewDestroyed = false;

        initViews(view);
        setupToolbar();
        setupRecyclerView();
        setupInputListeners();
        setupSuggestions();
        initGroqChat();
        showWelcomeMessage();
    }

    private void initGroqChat() {
        groqChatHelper = new GroqChatHelper();
    }

    private void initViews(View view) {
        toolbar = view.findViewById(R.id.toolbar);
        rvMessages = view.findViewById(R.id.rvMessages);
        etMessage = view.findViewById(R.id.etMessage);
        fabSend = view.findViewById(R.id.fabSend);
        scrollSuggestions = view.findViewById(R.id.scrollSuggestions);
        chipSuggestion1 = view.findViewById(R.id.chipSuggestion1);
        chipSuggestion2 = view.findViewById(R.id.chipSuggestion2);
        chipSuggestion3 = view.findViewById(R.id.chipSuggestion3);
    }

    private void setupToolbar() {
        toolbar.setNavigationOnClickListener(v ->
                Navigation.findNavController(requireView()).navigateUp());
    }

    private void setupRecyclerView() {
        adapter = new ChatAdapter(messages);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);
        rvMessages.setLayoutManager(layoutManager);
        rvMessages.setAdapter(adapter);
    }

    private void setupInputListeners() {
        fabSend.setOnClickListener(v -> sendMessage());

        etMessage.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendMessage();
                return true;
            }
            return false;
        });
    }

    private void setupSuggestions() {
        chipSuggestion1.setOnClickListener(v -> {
            etMessage.setText(chipSuggestion1.getText());
            sendMessage();
        });

        chipSuggestion2.setOnClickListener(v -> {
            etMessage.setText(chipSuggestion2.getText());
            sendMessage();
        });

        chipSuggestion3.setOnClickListener(v -> {
            etMessage.setText(chipSuggestion3.getText());
            sendMessage();
        });
    }

    private void showWelcomeMessage() {
        ChatMessage welcomeMessage = ChatMessage.createBotMessage(
                getString(R.string.ai_chat_welcome)
        );
        addMessage(welcomeMessage);
    }

    private void sendMessage() {
        String messageText = etMessage.getText() != null ?
                etMessage.getText().toString().trim() : "";

        if (messageText.isEmpty()) {
            return;
        }

        // Hide suggestions after first message
        scrollSuggestions.setVisibility(View.GONE);

        // Add user message
        ChatMessage userMessage = ChatMessage.createUserMessage(messageText);
        addMessage(userMessage);

        // Clear input
        etMessage.setText("");

        // Show typing indicator
        showTypingIndicator();

        // Get AI response (simulated for now)
        getAIResponse(messageText);
    }

    private void showTypingIndicator() {
        ChatMessage typingMessage = ChatMessage.createTypingIndicator();
        addMessage(typingMessage);
    }

    private void hideTypingIndicator() {
        for (int i = messages.size() - 1; i >= 0; i--) {
            if (messages.get(i).isTyping()) {
                messages.remove(i);
                adapter.notifyItemRemoved(i);
                break;
            }
        }
    }

    private void getAIResponse(String userMessage) {
        if (groqChatHelper == null || isViewDestroyed) return;

        groqChatHelper.sendMessage(userMessage, new GroqChatHelper.ChatCallback() {
            @Override
            public void onSuccess(String response) {
                if (getActivity() == null || isViewDestroyed) return;
                getActivity().runOnUiThread(() -> {
                    if (isViewDestroyed) return;
                    hideTypingIndicator();
                    ChatMessage botMessage = ChatMessage.createBotMessage(response);
                    addMessage(botMessage);
                });
            }

            @Override
            public void onError(String error) {
                if (getActivity() == null || isViewDestroyed) return;
                getActivity().runOnUiThread(() -> {
                    if (isViewDestroyed) return;
                    hideTypingIndicator();
                    ChatMessage errorMessage = ChatMessage.createErrorMessage(
                        "Sorry, I couldn't process your request. Please try again."
                    );
                    addMessage(errorMessage);
                });
            }
        });
    }

    private void addMessage(ChatMessage message) {
        messages.add(message);
        adapter.notifyItemInserted(messages.size() - 1);
        rvMessages.smoothScrollToPosition(messages.size() - 1);
    }

    // Chat Adapter
    private static class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private static final int TYPE_USER = 0;
        private static final int TYPE_BOT = 1;
        private static final int TYPE_TYPING = 2;

        private final List<ChatMessage> messages;

        ChatAdapter(List<ChatMessage> messages) {
            this.messages = messages;
        }

        @Override
        public int getItemViewType(int position) {
            ChatMessage message = messages.get(position);
            if (message.isTyping()) return TYPE_TYPING;
            if (message.isUser()) return TYPE_USER;
            return TYPE_BOT;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            switch (viewType) {
                case TYPE_USER:
                    return new UserMessageViewHolder(
                            inflater.inflate(R.layout.item_chat_message_user, parent, false));
                case TYPE_TYPING:
                    return new TypingViewHolder(
                            inflater.inflate(R.layout.item_chat_typing, parent, false));
                default:
                    return new BotMessageViewHolder(
                            inflater.inflate(R.layout.item_chat_message_bot, parent, false));
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ChatMessage message = messages.get(position);

            if (holder instanceof UserMessageViewHolder) {
                ((UserMessageViewHolder) holder).tvMessage.setText(message.getContent());
            } else if (holder instanceof BotMessageViewHolder) {
                ((BotMessageViewHolder) holder).tvMessage.setText(message.getContent());
            }
            // Typing indicator doesn't need binding
        }

        @Override
        public int getItemCount() {
            return messages.size();
        }

        static class UserMessageViewHolder extends RecyclerView.ViewHolder {
            TextView tvMessage;

            UserMessageViewHolder(View view) {
                super(view);
                tvMessage = view.findViewById(R.id.tvMessage);
            }
        }

        static class BotMessageViewHolder extends RecyclerView.ViewHolder {
            TextView tvMessage;

            BotMessageViewHolder(View view) {
                super(view);
                tvMessage = view.findViewById(R.id.tvMessage);
            }
        }

        static class TypingViewHolder extends RecyclerView.ViewHolder {
            TypingViewHolder(View view) {
                super(view);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isViewDestroyed = true;

        // Clear references to prevent memory leaks
        if (groqChatHelper != null) {
            groqChatHelper.clearConversation();
            groqChatHelper = null;
        }

        // Clear view references
        rvMessages = null;
        etMessage = null;
        fabSend = null;
        scrollSuggestions = null;
        chipSuggestion1 = null;
        chipSuggestion2 = null;
        chipSuggestion3 = null;
        toolbar = null;
        adapter = null;
    }
}
