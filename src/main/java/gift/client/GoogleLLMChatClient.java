package gift.client;

import org.springframework.ai.chat.client.ChatClient;

public class GoogleLLMChatClient implements LLMChatClient {
    private final ChatClient chatClient;

    public GoogleLLMChatClient(ChatClient.Builder builder, String systemPrompt) {
        this.chatClient = builder
                .defaultSystem(systemPrompt)
                .build();
    }
    @Override
    public String chat(String userMessage) {
        return chatClient.prompt()
                .user(userMessage)
                .call()
                .content();
    }
}
