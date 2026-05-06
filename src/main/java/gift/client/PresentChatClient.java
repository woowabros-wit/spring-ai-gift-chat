package gift.client;

import gift.client.dto.ChatRequest;
import gift.client.dto.ChatResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class PresentChatClient {

    private static final Logger log = LoggerFactory.getLogger(PresentChatClient.class);

    static final String SYSTEM_PROMPT = """
        너는 선물 추천 전문가야.
        - 누구에게 줄 선물인지, 예산, 취향 등 부족한 정보가 있으면 먼저 질문해.
        - 추천은 3개 이내로 간결하게 해줘.
        - 각 추천에 이유를 설명해줘.
        """;

    public ChatResponse chat(ChatRequest request) {
        try {
            return call(request);
        } catch (Exception e) {
            log.error("LLM 호출 중 에러가 발생하였습니다.", e);
            throw new IllegalStateException("선물 추천을 생성하는 중 오류가 발생하였습니다. 다시 시도해주세요.", e);
        }
    }

    abstract ChatResponse call(ChatRequest request);
}
