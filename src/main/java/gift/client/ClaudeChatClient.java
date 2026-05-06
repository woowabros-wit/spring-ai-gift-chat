package gift.client;

import gift.client.dto.ChatRequest;
import gift.client.dto.ChatResponse;
import java.nio.file.Path;
import org.springaicommunity.claude.agent.sdk.ClaudeClient;
import org.springaicommunity.claude.agent.sdk.ClaudeSyncClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "app.ai.provider", havingValue = "claude")
public class ClaudeChatClient extends PresentChatClient {

    private final ClaudeSyncClient client;

    public ClaudeChatClient(
        @Value("${claude.model}") String model,
        @Value("${claude.working-dir}") String workingDir
    ) {
        this.client = ClaudeClient.sync()
            .workingDirectory(Path.of(workingDir))
            .model(model)
            .build();
    }

    public ChatResponse call(ChatRequest request) {
        var message = request.message();
        var sessionId = getOrGenerateSessionId(request.sessionId());

        String content = client.connectText(message);
        return new ChatResponse(content, sessionId);
    }
}
