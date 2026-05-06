package gift;

import org.springaicommunity.claude.agent.sdk.ClaudeClient;
import org.springaicommunity.claude.agent.sdk.ClaudeSyncClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
@ConditionalOnProperty(name = "gift.llm.provider", havingValue = "claude")
public class ClaudeGiftChatClient implements GiftChatClient {

    @Override
    public String chat(String systemPrompt, String userMessage) {

        try (ClaudeSyncClient client = ClaudeClient.sync()
                .workingDirectory(Path.of("."))
                .model("claude-sonnet-4-20250514")
                .systemPrompt(systemPrompt)
                .build()) {

            return client.connectText(userMessage);
        }
    }
}