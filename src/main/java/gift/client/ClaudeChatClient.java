package gift.client;

import gift.client.dto.ChatRequest;
import gift.client.dto.ChatResponse;
import gift.utils.StringUtils;
import java.nio.file.Path;
import org.springaicommunity.claude.agent.sdk.ClaudeClient;
import org.springaicommunity.claude.agent.sdk.ClaudeSyncClient;
import org.springaicommunity.claude.agent.sdk.transport.CLIOptions;
import org.springaicommunity.claude.agent.sdk.types.AssistantMessage;
import org.springaicommunity.claude.agent.sdk.types.ResultMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "app.ai.provider", havingValue = "claude")
public class ClaudeChatClient extends PresentChatClient {

    private final String model;
    private final Path workingDirectory;

    public ClaudeChatClient(
        @Value("${claude.model}") String model,
        @Value("${claude.working-dir}") String workingDirectory
    ) {
        this.model = model;
        this.workingDirectory = Path.of(workingDirectory);
    }

    public ChatResponse call(ChatRequest request) {
        try (ClaudeSyncClient client = generateClaudeClient(request.sessionId())) {
            StringBuilder content = new StringBuilder();

            String sessionId = null;
            for (var response : client.connectAndReceive(request.message())) {
                switch (response) {
                    case AssistantMessage assistant -> content.append(assistant.text());
                    case ResultMessage result -> sessionId = result.sessionId();
                    default -> {}
                }
            }

            return new ChatResponse(content.toString(), sessionId);
        }
    }

    private ClaudeSyncClient generateClaudeClient(String sessionId) {
        CLIOptions.Builder builder = CLIOptions.builder()
            .model(model)
            .systemPrompt(SYSTEM_PROMPT);

        if (StringUtils.isNotBlank(sessionId)) {
            builder.resume(sessionId);
        }

        return ClaudeClient.sync(builder.build())
            .workingDirectory(workingDirectory)
            .build();
    }
}
