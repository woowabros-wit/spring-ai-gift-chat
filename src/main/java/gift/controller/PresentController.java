package gift.controller;

import io.micrometer.common.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PresentController {

    private static final Logger log = LoggerFactory.getLogger(PresentController.class);
    private static final String UNKNOWN_ID = "UNKNOWN";

    private final ChatClient chatClient;

    public PresentController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @GetMapping("/present")
    public String recommend(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        if (StringUtils.isBlank(message)) {
            return "메시지를 입력해주세요.";
        }

        long start = System.currentTimeMillis();
        var response = chatClient.prompt()
            .system("""
                너는 선물 추천 전문가야.
                - 누구에게 줄 선물인지, 예산, 취향 등 부족한 정보가 있으면 먼저 질문해.
                - 추천은 3개 이내로 간결하게 해줘.
                - 각 추천에 이유를 설명해줘.
                """)
            .user(message)
            .call();
        long elapsed = System.currentTimeMillis() - start;

        String sessionId = findSessionId(response);
        log.info("request id = {}, user input = {}, create response time = {}", sessionId, message, elapsed);

        return response.content();
    }

    private static String findSessionId(ChatClient.CallResponseSpec response) {
        var chatResponse = response.chatResponse();
        if (chatResponse == null) {
            return UNKNOWN_ID;
        }
        return chatResponse.getMetadata().getId();
    }
}
