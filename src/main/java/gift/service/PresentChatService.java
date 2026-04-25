package gift.service;

import gift.dto.PresentRequest;
import gift.dto.PresentResponse;
import gift.utils.SessionIdGenerator;
import gift.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;

@Service
public class PresentChatService {

    private static final Logger log = LoggerFactory.getLogger(PresentChatService.class);

    private final ChatClient chatClient;

    public PresentChatService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public PresentResponse chat(PresentRequest request) {
        var message = request.message();
        var sessionId = getOrGenerateSessionId(request.sessionId());

        var chatRequest = chatClient.prompt()
            .user(message)
            .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, sessionId));

        String content = callChatClient(chatRequest);
        if (StringUtils.isBlank(content)) {
            throw new IllegalStateException("LLM이 응답을 생성하지 못했습니다. 다시 시도해주세요.");
        }

        return new PresentResponse(content, sessionId);
    }

    private static String getOrGenerateSessionId(String sessionId) {
        return StringUtils.isBlank(sessionId) ? SessionIdGenerator.generate() : sessionId;
    }

    private String callChatClient(ChatClient.ChatClientRequestSpec chatRequest) {
        try {
            return chatRequest.call().content();
        } catch (Exception e) {
            log.error("LLM 호출 중 에러가 발생하였습니다.", e);
            throw new IllegalStateException("선물 추천을 생성하는 중 오류가 발생하였습니다. 다시 시도해주세요.", e);
        }
    }
}
