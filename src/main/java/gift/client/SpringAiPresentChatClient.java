package gift.client;

import gift.client.dto.ChatRequest;
import gift.client.dto.ChatResponse;
import gift.utils.RandomStringGenerator;
import gift.utils.StringUtils;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnExpression("'${app.ai.provider}' == 'bedrock' || '${app.ai.provider}' == 'google'")
public class SpringAiPresentChatClient extends PresentChatClient {

    private final ChatClient chatClient;

    public SpringAiPresentChatClient(ChatModel chatModel) {
        ChatMemory chatMemory = MessageWindowChatMemory.builder().build();
        this.chatClient = ChatClient.builder(chatModel)
            .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
            .defaultSystem(SYSTEM_PROMPT)
            .build();
    }

    public ChatResponse call(ChatRequest request) {
        var message = request.message();
        var sessionId = getOrGenerateSessionId(request.sessionId());

        var chatRequest = chatClient.prompt()
            .user(message)
            .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, sessionId));

        String content = chatRequest.call().content();
        return new ChatResponse(content, sessionId);
    }

    private String getOrGenerateSessionId(String sessionId) {
        return StringUtils.isBlank(sessionId) ? RandomStringGenerator.generate() : sessionId;
    }
}
