package gift.service;

import gift.client.LLMChatClient;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import gift.dto.ChatResponse;
import gift.exception.LLMInvalidResponseException;
import gift.exception.LLMUnavailableException;

import java.util.UUID;

@Service
public class ChatService {
    private final LLMChatClient llmChatClient;

    public static final String ERROR_MESSAGE = "다시 요청해주세요.";
    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);

    public ChatService(LLMChatClient llmChatClient) {
        this.llmChatClient = llmChatClient;
    }

    public ChatResponse chat(String message, UUID sessionId) {
        UUID requestId = UUID.randomUUID();

        long start = System.currentTimeMillis();
        String responseMessage;
        try {
            responseMessage = llmChatClient.chat(message);
        } catch (Exception ex) {
            logger.error("AI 호출 실패: sessionId={}, requestId={}, message={}", sessionId, requestId, message, ex);
            throw new LLMUnavailableException(ERROR_MESSAGE);
        }

        if (Strings.isBlank(responseMessage)) {
            logger.warn("AI 응답이 null: sessionId={}, requestId={}", sessionId, requestId);
            throw new LLMInvalidResponseException(ERROR_MESSAGE);
        }


        long durationMs = System.currentTimeMillis() - start;
        return new ChatResponse(requestId, responseMessage, durationMs, sessionId);
    }
}
