package gift.chat.service;

import static java.util.UUID.randomUUID;

import jakarta.validation.constraints.NotBlank;
import java.time.Duration;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

  private static final Logger log = LoggerFactory.getLogger(ChatService.class);
  private static final String SYSTEM_PROMPT = "당신은 고객의 취향에 맞는 선물을 추천하는 선물 추천 전문가입니다.";
  private static final String FALLBACK_MESSAGE = "지금은 선물 추천을 준비 중입니다. 잠시 후 다시 시도해 주세요.";
  private final ChatClient chatClient;

  public ChatService(ChatClient.Builder builder) {
    this.chatClient = builder.build();
  }

  public MessageRequest sendMessage(UUID sessionId, @NotBlank String message) {
    UUID requestId = randomUUID();
    long startTime = System.nanoTime();

    try {
      String response = chatClient.prompt()
          .system(SYSTEM_PROMPT)
          .user(message)
          .call()
          .content();
      return createResponse(sessionId, requestId, message, response, startTime);
    } catch (RuntimeException exception) {
      return createResponse(sessionId, requestId, message, FALLBACK_MESSAGE, startTime);
    }
  }

  private MessageRequest createResponse(
      UUID sessionId,
      UUID requestId,
      String userMessage,
      String responseMessage,
      long startTime
  ) {
    long durationMs = Duration.ofNanos(System.nanoTime() - startTime).toMillis();
    log.info(
        "requestId={}, sessionId={}, userMessage={}, durationMs={}",
        requestId,
        sessionId,
        userMessage,
        durationMs
    );
    return new MessageRequest(requestId, responseMessage, durationMs);
  }
}
