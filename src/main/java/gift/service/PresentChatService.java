package gift.service;

import gift.dto.PresentRequest;
import gift.dto.PresentResponse;
import gift.utils.SessionIdGenerator;
import io.micrometer.common.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.stereotype.Service;

@Service
public class PresentChatService {

    private static final Logger log = LoggerFactory.getLogger(PresentChatService.class);

    private final ChatClient chatClient;

    public PresentChatService(ChatClient.Builder builder) {
        ChatMemory chatMemory = MessageWindowChatMemory.builder().build();
        this.chatClient = builder
            .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
            .defaultSystem("""
                너는 선물 추천 전문가야.
                - 누구에게 줄 선물인지, 예산, 취향 등 부족한 정보가 있으면 먼저 질문해.
                - 추천은 3개 이내로 간결하게 해줘.
                - 각 추천에 이유를 설명해줘.
                """)
            .build();
    }

    public PresentResponse chat(PresentRequest request) {
        var message = request.message();
        String sessionId = SessionIdGenerator.generateSessionId(request.sessionId());

        long start = System.currentTimeMillis();
        try {
            var response = chatClient.prompt()
                .user(message)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, sessionId))
                .call();
            return PresentResponse.ok(response.content(), sessionId);

        } catch (Exception e) {
            log.error("LLM 호출 중 에러가 발생하였습니다.", e);
            throw new IllegalStateException("선물 추천을 생성하는 중 오류가 발생하였습니다. 다시 시도해주세요.", e);

        } finally {
            long elapsed = System.currentTimeMillis() - start;
            log.info("request id = {}, user input = {}, create response time = {}", sessionId, message, elapsed);
        }
    }
}
