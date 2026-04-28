package gift.service;

import gift.controller.AiGiftRecommendRequest;
import gift.controller.AiGiftRecommendResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.util.UUID;

@Service
public class AiGiftRecommendService {

    private final ChatClient chatClient;

    public AiGiftRecommendService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    public AiGiftRecommendResponse recommend(AiGiftRecommendRequest request) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        String content = chatClient.prompt()
                .user(request.message())
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
