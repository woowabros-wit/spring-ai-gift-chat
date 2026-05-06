package gift.chat.external

import gift.chat.service.LoggerAdvisor
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.ChatClient.Builder
import java.util.UUID

class SpringAiGiftRecommendChatClient(
    chatClientBuilder: Builder,
    systemPrompt: String,
): GiftRecommendChatClient {
    private val chatClient: ChatClient = chatClientBuilder
        .defaultSystem(systemPrompt)
        .defaultAdvisors(LoggerAdvisor())
        .build()

    override fun call(message: String, sessionId: String?): Result<ChatResponse> {
        val resolvedSessionId = sessionId ?: UUID.randomUUID().toString()
        return runCatching {
            val content = chatClient.prompt()
                .user(message)
                .advisors { it.param(ADVISOR_SESSION_ID_KEY, resolvedSessionId) }
                .call()
                .content() ?: error("content is empty")
            ChatResponse(sessionId = resolvedSessionId, message = content)
        }
    }

    companion object {
        private const val ADVISOR_SESSION_ID_KEY = "sessionId"
    }
}
