package gift.service;

import gift.client.PresentChatClient;
import gift.client.dto.ChatRequest;
import gift.controller.dto.PresentRequest;
import gift.controller.dto.PresentResponse;
import gift.utils.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class PresentChatService {

    private final PresentChatClient presentChatClient;

    public PresentChatService(PresentChatClient presentChatClient) {
        this.presentChatClient = presentChatClient;
    }

    public PresentResponse chat(PresentRequest request) {
        var chatRequest = new ChatRequest(request.message(), request.sessionId());
        var chatResponse = presentChatClient.chat(chatRequest);
        if (StringUtils.isBlank(chatResponse.message())) {
            throw new IllegalStateException("LLM이 응답을 생성하지 못했습니다. 다시 시도해주세요.");
        }
        return new PresentResponse(chatResponse.message(), chatResponse.sessionId());
    }
}
