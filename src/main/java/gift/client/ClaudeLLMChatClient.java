package gift.client;

import org.springaicommunity.claude.agent.sdk.Query;
import org.springaicommunity.claude.agent.sdk.QueryOptions;

public class ClaudeLLMChatClient implements LLMChatClient {
    private final QueryOptions queryOptions;

    public ClaudeLLMChatClient(String systemPrompt) {
        this.queryOptions = QueryOptions.builder()
                .appendSystemPrompt(systemPrompt)
                .build();
    }

    @Override
    public String chat(String userMessage) {
        return Query.text(userMessage, queryOptions);
    }
}
