package gift.service;

import gift.controller.AiGiftRecommendRequest;
import gift.controller.AiGiftRecommendResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.util.UUID;

@Service
public class AiGiftRecommendService {

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
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        String content = chatClient.prompt()
                .user(request.message())
                .system(aiRoleResource)
                .call()
                .content();
        stopWatch.stop();
        return new AiGiftRecommendResponse(
                UUID.randomUUID().toString(),
                content,
                request.sessionId(),
                stopWatch.getTotalTimeMillis()
        );
    }
}
