package gift.chat;

import gift.prompt.PromptLoader;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    private final ChatClient client;

    public ChatService(ChatClient.Builder builder, PromptLoader promptLoader) {
        this.client = builder
            .defaultSystem(promptLoader.loadSystemPrompt())
            .build();
    }

    public String chat(String message) {
        try {
            return client.prompt().user(message).call().content();
        } catch (Exception e) {
            throw new ChatClientException(e);
        }
    }
}
