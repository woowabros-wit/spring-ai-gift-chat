package gift.client;

import gift.advisor.SimpleLogWithTimeAdvisor;
import gift.client.dto.ChatRequest;
import gift.client.dto.ChatResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "app.ai.provider", havingValue = "google")
public class GeminiChatClient extends PresentChatClient {

    private final ChatClient chatClient;

    public GeminiChatClient(ChatModel chatModel) {
        ChatMemory chatMemory = MessageWindowChatMemory.builder().build();
        this.chatClient = ChatClient.builder(chatModel)
            .defaultAdvisors(
                MessageChatMemoryAdvisor.builder(chatMemory).build(),
                new SimpleLogWithTimeAdvisor()
            )
            .defaultSystem(SYSTEM_PROMPT)
            .build();
    }

    public ChatResponse call(ChatRequest request) {
        var message = request.message();
        var sessionId = getOrGenerateSessionId(request.sessionId());

        var chatRequest = chatClient.prompt()
            .user(message)
            .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, sessionId));

        String content = chatRequest.call().content();
        return new ChatResponse(content, sessionId);
    }
}
