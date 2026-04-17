package gift.controller;

import gift.dto.PresentRequest;
import gift.dto.PresentResponse;
import io.micrometer.common.util.StringUtils;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PresentController {

    private static final Logger log = LoggerFactory.getLogger(PresentController.class);

    private final ChatClient chatClient;

    public PresentController(ChatClient.Builder builder) {
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

    @PostMapping("/present")
    public PresentResponse recommend(
        @RequestBody PresentRequest request
    ) {
        var message = request.message();
        if (StringUtils.isBlank(message)) {
            return PresentResponse.error("메시지를 입력해주세요.", request.sessionId());
        }

        String sessionId = StringUtils.isBlank(request.sessionId())
            ? UUID.randomUUID().toString()
            : request.sessionId();

        long start = System.currentTimeMillis();
        var response = chatClient.prompt()
            .user(message)
            .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, sessionId))
            .call();
        long elapsed = System.currentTimeMillis() - start;

        log.info("request id = {}, user input = {}, create response time = {}", sessionId, message, elapsed);

        return PresentResponse.ok(response.content(), sessionId);
    }
}
