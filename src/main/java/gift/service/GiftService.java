package gift.service;

import gift.dto.GiftReq;
import gift.dto.GiftRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
public class GiftService {
    /* Logger */
    private static final Logger log = LoggerFactory.getLogger(GiftService.class);
    private final ChatClient client;

    public GiftService(ChatClient.Builder builder) {
        this.client = builder.build();
    }

    public GiftRes chat(GiftReq req) {
        UUID requestId = UUID.randomUUID();
        log.info("message: {}, sessionId: {}, requestId: {}", req.message(), req.sessionId(), requestId);

        String systemPrompt = """
                당신은 선물 고민을 이야기하면 대화를 통해 적절히 선물을 추천해주는 도우미 입니다.
                예시) "친구 생일 선물 추천 줘", "친구 선물을 주고 싶은데 추천해줄래?"
                
                - 사용자에게 적절한 가격이 얼마인지 확인
                - 친구의 선물 취향 등을 확인
                - 그외에 추가로 판단이 필요한 부분은 사용자에게 질문한다.
                """;

        Instant start = Instant.now();
        try {
            String content = client
                    .prompt()
                    .system(systemPrompt)
                    .user(req.message())
                    .call()
                    .content();

            long durationMs = Duration.between(start, Instant.now()).toMillis();
            return new GiftRes(requestId.toString(), content, durationMs);
        } catch (Exception e) {
            throw new IllegalStateException("선물 추천에 실패했습니다", e);
        }
    }
}
