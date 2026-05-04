package gift.service;

import gift.controller.AiGiftRecommendRequest;
import gift.controller.AiGiftRecommendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.util.UUID;

@Service
public class AiGiftRecommendService {

    private static final Logger log = LoggerFactory.getLogger(AiGiftRecommendService.class);

    static final String FALLBACK_MESSAGE = "죄송합니다. 일시적으로 추천을 드릴 수 없습니다. 잠시 후 다시 시도해 주세요.";

    private final ChatClient chatClient;
    private final Resource aiRoleResource;

    public AiGiftRecommendService(
            ChatClient.Builder builder,
            @Value("classpath:ai-role.txt") Resource aiRoleResource
    ) {
        this.chatClient = builder.build();
        this.aiRoleResource = aiRoleResource;
    }

    public AiGiftRecommendResponse recommend(AiGiftRecommendRequest request) {
        String requestId = UUID.randomUUID().toString();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        String content;
        try {
            content = chatClient.prompt()
                    .user(request.message())
                    .system(aiRoleResource)
                    .call()
                    .content();
        } catch (Exception e) {
            log.error("LLM 호출 중 오류가 발생했습니다", e);
            content = FALLBACK_MESSAGE;
        }
        stopWatch.stop();
        long durationMs = stopWatch.getTotalTimeMillis();

        log.info("requestId={}, sessionId={}, durationMs={}", requestId, request.sessionId(), durationMs);

        return new AiGiftRecommendResponse(
                requestId,
                content,
                request.sessionId(),
                durationMs
        );
    }
}
