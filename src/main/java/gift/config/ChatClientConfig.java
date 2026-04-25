package gift.config;

import gift.advisor.SimpleLogWithTimeAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfig {

    private static final String SYSTEM_PROMPT = """
        너는 선물 추천 전문가야.
        - 누구에게 줄 선물인지, 예산, 취향 등 부족한 정보가 있으면 먼저 질문해.
        - 추천은 3개 이내로 간결하게 해줘.
        - 각 추천에 이유를 설명해줘.
        """;

    @Bean
    @ConditionalOnProperty(name = "app.ai.provider", havingValue = "google")
    public ChatClient googleChatClient(ChatModel googleGenAiChatModel) {
        return buildChatClient(googleGenAiChatModel);
    }

    @Bean
    @ConditionalOnProperty(name = "app.ai.provider", havingValue = "bedrock")
    public ChatClient bedrockChatClient(ChatModel bedrockConverseChatModel) {
        return buildChatClient(bedrockConverseChatModel);
    }

    private ChatClient buildChatClient(ChatModel chatModel) {
        ChatMemory chatMemory = MessageWindowChatMemory.builder().build();
        return ChatClient.builder(chatModel)
            .defaultAdvisors(
                MessageChatMemoryAdvisor.builder(chatMemory).build(),
                new SimpleLogWithTimeAdvisor()
            )
            .defaultSystem(SYSTEM_PROMPT)
            .build();
    }
}
